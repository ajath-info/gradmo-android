package com.app.edtech.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentFilterInstituteBottomSheetBinding
import com.app.edtech.databinding.FragmentUpcomingExamListBinding
import com.app.edtech.databinding.FragmentVideoLecturesBinding
import com.app.edtech.model.institute_list.request.InstitutesListRequest
import com.app.edtech.model.institute_list.response.InstituteListResponse
import com.app.edtech.model.login.response.LoginResponse
import com.app.edtech.preferences.LOGIN_DATA
import com.app.edtech.preferences.Preferences
import com.app.edtech.ui.activity.HomeActivity
import com.app.edtech.ui.adapter.AdapterUpcomingExams
import com.app.edtech.ui.adapter.AdapterVideoLecture
import com.app.edtech.ui.view_model.UpcomingExamsViewModel
import com.app.edtech.ui.view_model.VideoLectureViewModel
import com.app.edtech.utils.CommonUtils
import com.app.edtech.utils.CommonUtils.updateModeUI
import com.app.edtech.utils.CommonUtils.updateSortUI
import com.app.edtech.utils.network_utils.ProcessDialog
import com.app.edtech.utils.network_utils.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue


@AndroidEntryPoint
class UpcomingExamListFragment : BaseFragment<FragmentUpcomingExamListBinding>() {
    private var totalRecords: Int = 0
    private var currentPage = 1
    private val pageSize = 10   // adjust as per API
    private var isLoading = false
    private var isLastPage = false

    private val BooksList = mutableListOf<InstituteListResponse.Institute>()
    private val viewModel: UpcomingExamsViewModel by viewModels()
    private lateinit var libraryBooksAdapter: AdapterUpcomingExams  // replace with your adapter

    private var orderType = "DESC"   // default Z-A
    private var selectedMode = ""    // "online" / "offline" / ""
    private var flow = ""    // "online" / "offline" / ""

    private var searchRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())
    private var searchQuery: String = ""

    override fun initView(savedInstanceState: Bundle?) {
        flow = arguments?.getString("flow", "") ?: ""
        Log.i("TAG", "flow: "+flow)
        setupUI()
        currentPage = 1
        isLastPage = false
        BooksList.clear()

//        replace with library api, and set adapter from inside of api response
        setupBooksRecycler(listOf(), false, totalRecords)
//        callInstituteApi()
    }

    private fun setupUI() {
        when(flow){
            "seeAll"->{
                binding.title.text = "Institutes"
                (activity as HomeActivity).hideNavigationView()
            }
        }
    }

    private fun setupBooksRecycler(institutes: List<InstituteListResponse.Institute>, isRestore:Boolean, totalRecords:Int) {
        libraryBooksAdapter = AdapterUpcomingExams(BooksList, ::onBookSelected)
        binding.instituteRecycler.apply {
            adapter = libraryBooksAdapter
        }
        addPaginationScroll()
//        binding.tvShowingCount.text = "Showing ${BooksList.size} of ${totalRecords} results"
        if (!isRestore){
            val start = BooksList.size
            BooksList.addAll(institutes)
            libraryBooksAdapter.notifyItemRangeInserted(start, institutes.size)
            isLoading = false

            if (institutes.size < pageSize) {
                isLastPage = true
            }
//            binding.tvShowingCount.text = "Showing ${BooksList.size} of ${totalRecords} results"
        }
    }

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
    private fun onBookSelected(/*institute:InstituteListResponse.Institute*/) {
//        var bundle = Bundle()
//        bundle.putParcelable("institute", institute)
        findNavController().navigate(R.id.videoLectureDetailFragment/*, bundle*/)
    }

    private fun clickEvent() {
        binding.backButton.setOnClickListener{
            findNavController().popBackStack()
        }
        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchQuery = binding.searchField.text.toString().trim()

                currentPage = 1
                isLastPage = false
                BooksList.clear()
                libraryBooksAdapter.notifyDataSetChanged()

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
                        BooksList.clear()
                        libraryBooksAdapter.notifyDataSetChanged()

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
    private fun applyFilters() {
        currentPage = 1
        isLastPage = false
        BooksList.clear()
        libraryBooksAdapter.notifyDataSetChanged()

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
            order_type = orderType,     // ✅ dynamic
            mode = selectedMode,        // ✅ dynamic
            page = currentPage.toString(),
            limit = pageSize.toString(),
            search = searchQuery,
        )

        viewModel.hitInstitutesDataApi("Bearer $accessToken", request)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        clickEvent()
    }
    override fun getLayoutId(): Int = R.layout.fragment_upcoming_exam_list
    private fun setObserver() {
        viewModel.getInstitutesLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "Login success: ${Gson().toJson(it)}")
                    if (it.data?.status == "true") {
                        totalRecords = it.data.pagination.totalRecords
                        setupBooksRecycler(it.data.institutes, false, totalRecords)
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.msg}", Toast.LENGTH_SHORT)
                            .show()
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Login Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }

    }
    override fun restoreView() {
        //        after api implement uncomment api and remove setupBooksRecycler()
//        viewModel.getInstitutesLiveData().value?.data?.institutes?.let {
//            setupBooksRecycler(it, true, totalRecords)
//        }
        setupBooksRecycler(listOf(), false, totalRecords)
    }

}