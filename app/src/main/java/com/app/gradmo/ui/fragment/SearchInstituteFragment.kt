package com.app.gradmo.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentFilterInstituteBottomSheetBinding
import com.app.gradmo.databinding.FragmentSearchInstituteBinding
import com.app.gradmo.model.banner.response.BannerResponse
import com.app.gradmo.model.institute_list.request.InstitutesListRequest
import com.app.gradmo.model.institute_list.response.InstituteListResponse
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.activity.HomeActivity
import com.app.gradmo.ui.adapter.HomeBannerAdapter
import com.app.gradmo.ui.adapter.SearchInstituteAdapter
import com.app.gradmo.ui.view_model.SearchInstituteViewModel
import com.app.gradmo.utils.CommonUtils
import com.app.gradmo.utils.CommonUtils.updateModeUI
import com.app.gradmo.utils.CommonUtils.updateSortUI
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.ListPopupWindow

@AndroidEntryPoint
class SearchInstituteFragment : BaseFragment<FragmentSearchInstituteBinding>() {
    private var totalRecords: Int = 0
    private var currentPage = 1
    private val pageSize = 10   // adjust as per API
    private var isLoading = false
    private var isLastPage = false

    private val instituteList = mutableListOf<InstituteListResponse.Institute>()
    private val viewModel: SearchInstituteViewModel by viewModels()
    private lateinit var searchInstituteAdapter: SearchInstituteAdapter  // replace with your adapter
    private lateinit var homeBannerAdapter: HomeBannerAdapter  // replace with your adapter

    private var orderType = "DESC"   // default Z-A
    private var selectedMode = ""    // "online" / "offline" / ""
    private var flow = ""    // "online" / "offline" / ""

    private var searchRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())
    private var searchQuery: String = ""
    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private val AUTO_SCROLL_DELAY = 7000L  // 3 seconds

    private var selectedCity = ""              // empty = no city filter
    private var cityList: List<String> = emptyList()

    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            val itemCount = homeBannerAdapter.itemCount
            if (itemCount == 0) return
            val nextItem = binding.bannerViewPager.currentItem + 1
            binding.bannerViewPager.setCurrentItem(nextItem, true)
            autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        flow = arguments?.getString("flow", "") ?: ""
        Log.i("TAG", "flow: "+flow)
        setupUI()
        setupInstitutesRecycler()
        currentPage = 1
        isLastPage = false
        instituteList.clear()
        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.accessToken
        viewModel.hitBannerDataApi("Bearer $accessToken")
        viewModel.hitCitiesDataApi("Bearer $accessToken")
        callInstituteApi()
    }

    private fun setupUI() {
        when(flow){
            "seeAll"->{
                binding.title.text = "Institutes"
                (activity as HomeActivity).hideNavigationView()
            }
        }
    }

    fun setUpBannerViewPager(banners: List<BannerResponse.Data.Banner>) {
        homeBannerAdapter = HomeBannerAdapter(banners)
        binding.bannerViewPager.adapter = homeBannerAdapter

        // Start at the middle for infinite feel
        val startPos = homeBannerAdapter.getStartPosition()
        binding.bannerViewPager.setCurrentItem(startPos, false)

        setupDotIndicator(banners.size)
        setupPageChangeListener(banners.size)
        startAutoScroll()
    }
    private val dots = mutableListOf<ImageView>()

    // ── Dots ─────────────────────────────────────────────────────────────
    private fun setupDotIndicator(count: Int) {
        dots.clear()
        binding.dotsIndicator.removeAllViews()

        repeat(count) { index ->
            val dot = ImageView(requireContext()).apply {
                setImageResource(
                    if (index == 0) R.drawable.dot_active else R.drawable.dot_inactive
                )
                val size = 10.dpToPx()
                val params = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(4.dpToPx(), 0, 4.dpToPx(), 0)
                }
                layoutParams = params
            }
            dots.add(dot)
            binding.dotsIndicator.addView(dot)
        }
    }

    private fun updateDots(realPosition: Int) {
        dots.forEachIndexed { index, dot ->
            dot.setImageResource(
                if (index == realPosition) R.drawable.dot_active else R.drawable.dot_inactive
            )
        }
    }

    private fun setupPageChangeListener(realCount: Int) {
        binding.bannerViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val realPos = homeBannerAdapter.getRealPosition(position)
                updateDots(realPos)
            }
        })
    }

    // ── Auto Scroll Controls ─────────────────────────────────────────────
    private fun startAutoScroll() {
        autoScrollHandler.removeCallbacks(autoScrollRunnable)
        autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY)
    }

    private fun stopAutoScroll() {
        autoScrollHandler.removeCallbacks(autoScrollRunnable)
    }

    // Stop when fragment is not visible, resume when it is
    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }

    override fun onResume() {
        super.onResume()
        if (::homeBannerAdapter.isInitialized) startAutoScroll()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAutoScroll()
    }

    // ── Extension ────────────────────────────────────────────────────────
    private fun Int.dpToPx(): Int =
        (this * resources.displayMetrics.density).toInt()

    // Call once in initView / onViewCreated
    private fun setupInstitutesRecycler() {
        searchInstituteAdapter = SearchInstituteAdapter(instituteList, ::onInstituteSelected)
        binding.instituteRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext()) // make sure this is set
            adapter = searchInstituteAdapter
        }
        addPaginationScroll()
    }

    // Call every time a new page arrives
    private fun appendInstitutes(newItems: List<InstituteListResponse.Institute>, totalRecords: Int) {
        val start = instituteList.size
        instituteList.addAll(newItems)
        searchInstituteAdapter.notifyItemRangeInserted(start, newItems.size)
        isLoading = false

        if (newItems.size < pageSize) isLastPage = true

        binding.tvShowingCount.text = "Showing ${instituteList.size} of $totalRecords results"
    }
    /*private fun setupInstitutesRecycler(institutes: List<InstituteListResponse.Institute>, isRestore:Boolean, totalRecords:Int) {
        searchInstituteAdapter = SearchInstituteAdapter(instituteList, ::onInstituteSelected)
        binding.instituteRecycler.apply {
            adapter = searchInstituteAdapter
        }
        addPaginationScroll()
        binding.tvShowingCount.text = "Showing ${instituteList.size} of ${totalRecords} results"
        if (!isRestore){
            val start = instituteList.size
            instituteList.addAll(institutes)
            searchInstituteAdapter.notifyItemRangeInserted(start, institutes.size)
            isLoading = false

            if (institutes.size < pageSize) {
                isLastPage = true
            }
            binding.tvShowingCount.text = "Showing ${instituteList.size} of ${totalRecords} results"
        }
    }*/

    private fun addPaginationScroll() {
        val layoutManager = binding.instituteRecycler.layoutManager as LinearLayoutManager

        binding.instituteRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                    ) {
                        loadNextPage()
                    }
                }
            }
        })
    }
    private fun loadNextPage() {
        isLoading = true
        currentPage++
        callInstituteApi()
    }
    private fun onInstituteSelected(institute:InstituteListResponse.Institute) {
        var bundle = Bundle()
        bundle.putParcelable("institute", institute)
        findNavController().navigate(R.id.instituteDetailsFragment, bundle)
    }

    private fun clickEvent() {
        binding.backButton.setOnClickListener{
//            findNavController().popBackStack()
            (activity as HomeActivity).popBackToHome()
        }
        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchQuery = binding.searchField.text.toString().trim()

                currentPage = 1
                isLastPage = false
                instituteList.clear()
                searchInstituteAdapter.notifyDataSetChanged()

                callInstituteApi()
                true
            } else false
        }
        binding.searchField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchRunnable?.let { handler.removeCallbacks(it) }

                searchRunnable = Runnable {
                    val query = s.toString().trim()

                    if (query != searchQuery) {
                        searchQuery = query

                        // 🔁 Reset pagination
                        currentPage = 1
                        isLastPage = false
                        instituteList.clear()
                        searchInstituteAdapter.notifyDataSetChanged()

                        callInstituteApi()
                    }
                }

                handler.postDelayed(searchRunnable!!, 500) // ⏳ debounce 500ms
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.filterLayout.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun showBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val sheetBinding = FragmentFilterInstituteBottomSheetBinding.inflate(layoutInflater)

        dialog.setContentView(sheetBinding.root)

        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }

        // ================= SORT BY =================

        sheetBinding.tvAZ.setOnClickListener {
            orderType = "ASC"
            CommonUtils.updateSortUI(sheetBinding, isAsc = true)
        }

        sheetBinding.tvZA.setOnClickListener {
            orderType = "DESC"
            CommonUtils.updateSortUI(sheetBinding, isAsc = false)
        }

        // ================= MODE =================

        sheetBinding.tvOffline.setOnClickListener {
            selectedMode = "offline"
            CommonUtils.updateModeUI(sheetBinding, isOffline = true)
        }

        sheetBinding.tvOnline.setOnClickListener {
            selectedMode = "online"
            CommonUtils.updateModeUI(sheetBinding, isOffline = false)
        }

        // ================= CITY =================

        sheetBinding.tvCity.text = if (selectedCity.isEmpty()) "Select Your City" else selectedCity

        sheetBinding.tvCity.setOnClickListener {
            showCityPopup(sheetBinding.tvCity)
        }

        // ================= APPLY =================

        sheetBinding.nextButton.setOnClickListener {
            dialog.dismiss()
            applyFilters()
        }

        sheetBinding.closeButton.setOnClickListener {
            dialog.dismiss()
        }
// Set previously selected SORT
        updateSortUI(sheetBinding, isAsc = orderType == "ASC")

// Set previously selected MODE
        when (selectedMode) {
            "offline" -> updateModeUI(sheetBinding, isOffline = true)
            "online" -> updateModeUI(sheetBinding, isOffline = false)
            else -> {
                // nothing selected → reset both
                sheetBinding.tvOffline.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.unselected_icon, 0, 0, 0
                )
                sheetBinding.tvOnline.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.unselected_icon, 0, 0, 0
                )
            }
        }
        dialog.show()
    }
    private fun showCityPopup(anchor: TextView) {
        if (cityList.isEmpty()) {
            Toast.makeText(requireContext(), "Cities not loaded yet, please try again", Toast.LENGTH_SHORT).show()
            return
        }

        val items = mutableListOf("All Cities").apply { addAll(cityList) }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)

        val popupWindow = ListPopupWindow(requireContext()).apply {
            setAdapter(adapter)
            anchorView = anchor
            width = anchor.width
            isModal = true
        }

        popupWindow.setOnItemClickListener { _, _, position, _ ->
            selectedCity = if (position == 0) "" else items[position]
            anchor.text = if (selectedCity.isEmpty()) "Select Your City" else selectedCity
            popupWindow.dismiss()
        }

        popupWindow.show()
    }
    private fun applyFilters() {
        currentPage = 1
        isLastPage = false
        instituteList.clear()
        searchInstituteAdapter.notifyDataSetChanged()

        callInstituteApi()
    }
    private fun callInstituteApi() {
        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(
            requireContext(),
            LOGIN_DATA
        )?.data?.accessToken

        val request = InstitutesListRequest(
            latitude = "28.93466857138595",
            longitude = "78.34283781396569",
            order_field = "name",
            order_type = orderType,
            mode = selectedMode,
            page = currentPage.toString(),
            limit = pageSize.toString(),
            search = searchQuery,
            city = selectedCity
        )

        viewModel.hitInstitutesDataApi("Bearer $accessToken", request)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        clickEvent()
    }
    override fun getLayoutId(): Int = R.layout.fragment_search_institute
    private fun setObserver() {
        viewModel.getBannerLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.status == "true") {
                        setUpBannerViewPager(it.data.data.banners)
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog(true)
                }
            }
        }

        viewModel.getInstitutesLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.status == "true") {
                        totalRecords = it.data.pagination.totalRecords
                        appendInstitutes(it.data.institutes, totalRecords) // ← changed
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.msg}", Toast.LENGTH_SHORT).show()
                    }
                    ProcessDialog.dismissDialog(true)
                }
                Status.LOADING -> if (currentPage==1) ProcessDialog.showDialog(requireContext(), true)
                Status.ERROR -> {
                    Log.e("TAG", "Login Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
        viewModel.getCitiesLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.status == "true") {
                        cityList = it.data.cities.map { c -> c.city }
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.msg}", Toast.LENGTH_SHORT).show()
                    }
                    ProcessDialog.dismissDialog(true)
                }
                Status.LOADING -> ProcessDialog.showDialog(requireContext(), true)
                Status.ERROR -> {
                    Log.e("TAG", "Cities Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
    }
    override fun restoreView() {
        binding.instituteRecycler.adapter = searchInstituteAdapter
        binding.tvShowingCount.text = "Showing ${instituteList.size} of $totalRecords results"

        viewModel.getBannerLiveData().value?.data?.data?.banners?.let {
            setUpBannerViewPager(it)
        }
    }

}