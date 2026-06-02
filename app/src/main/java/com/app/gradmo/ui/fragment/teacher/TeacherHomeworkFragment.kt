package com.app.gradmo.ui.fragment.teacher

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
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentFilterInstituteBottomSheetBinding
import com.app.gradmo.databinding.FragmentHomeworkBinding
import com.app.gradmo.databinding.FragmentTeacherHomeworkBinding
import com.app.gradmo.model.homework.HomeworkListResponse
import com.app.gradmo.model.library_list.LibraryListRequest
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.activity.HomeActivity
import com.app.gradmo.ui.adapter.AdapterHomework
import com.app.gradmo.ui.view_model.HomeworkViewModel
import com.app.gradmo.utils.CommonUtils
import com.app.gradmo.utils.CommonUtils.updateModeUI
import com.app.gradmo.utils.CommonUtils.updateSortUI
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class TeacherHomeworkFragment : BaseFragment<FragmentTeacherHomeworkBinding>() {
    private var totalRecords: Int = 0
    private var currentPage = 1
    private val pageSize = 10   // adjust as per API
    private var isLoading = false
    private var isLastPage = false

    private val BooksList = mutableListOf<HomeworkListResponse.HomeWork>()
    private val viewModel: HomeworkViewModel by viewModels()
    private lateinit var libraryBooksAdapter: AdapterHomework  // replace with your adapter

    private var orderType = "DESC"   // default Z-A
    private var selectedMode = ""    // "online" / "offline" / ""
    private var flow = ""    // "online" / "offline" / ""
    private var batch_id = ""

    private var searchRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())
    private var searchQuery: String = ""

    override fun initView(savedInstanceState: Bundle?) {
        flow = arguments?.getString("flow", "") ?: ""
        batch_id = arguments?.getString("batch_id", "") ?: ""
        Log.i("TAG", "batch_id: "+batch_id)
        Log.i("TAG", "flow: "+flow)
        setupUI()
        currentPage = 1
        isLastPage = false
        BooksList.clear()

//        replace with library api, and set adapter from inside of api response
        setupBooksRecycler(listOf(), false, totalRecords)
        callHomeworkApi()
    }

    private fun setupUI() {
        when(flow){
            "seeAll"->{
                binding.title.text = "Institutes"
                (activity as HomeActivity).hideNavigationView()
            }
        }
    }

    private fun setupBooksRecycler(institutes: List<HomeworkListResponse.HomeWork>, isRestore:Boolean, totalRecords:Int) {
        libraryBooksAdapter = AdapterHomework(BooksList, ::onBookSelected)
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
        callHomeworkApi()
    }
    private fun onBookSelected(homework: HomeworkListResponse.HomeWork) {
        var bundle = Bundle()
        bundle.putParcelable("homework", homework)
        findNavController().navigate(R.id.homeworkDetailFragment, bundle)
    }

    private fun clickEvent() {
        binding.createButton.setOnClickListener {
            findNavController().navigate(R.id.createHomeworkFragment)
        }
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

                callHomeworkApi()
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

                        callHomeworkApi()
                    }
                }

                handler.postDelayed(searchRunnable!!, 500) // ⏳ debounce 500ms
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.filterLayout.setOnClickListener {
//            showBottomSheet()
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

        callHomeworkApi()
    }
    private fun callHomeworkApi() {
        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(
            requireContext(),
            LOGIN_DATA
        )?.data?.accessToken

        val request = LibraryListRequest(
            batch_id =batch_id
        )

        viewModel.hitHomeworkDataApi("Bearer $accessToken", request)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        clickEvent()
    }
    override fun getLayoutId(): Int = R.layout.fragment_teacher_homework
    private fun setObserver() {
        viewModel.getHomeworkLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "getHomeworkLiveData success: ${Gson().toJson(it)}")
                    if (it.data?.status == "true") {
                        totalRecords = it.data.pagination.totalRecords
                        setupBooksRecycler(it.data.homeWork, false, totalRecords)
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
        viewModel.getHomeworkLiveData().value?.data?.homeWork?.let {
            setupBooksRecycler(it, true, totalRecords)
        }
//        setupBooksRecycler(listOf(), false, totalRecords)
    }

}