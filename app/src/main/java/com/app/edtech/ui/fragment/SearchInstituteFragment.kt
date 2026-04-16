package com.app.edtech.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentFilterInstituteBottomSheetBinding
import com.app.edtech.databinding.FragmentSearchInstituteBinding
import com.app.edtech.model.institute_list.request.InstitutesListRequest
import com.app.edtech.model.institute_list.response.InstituteListResponse
import com.app.edtech.model.login.response.LoginResponse
import com.app.edtech.preferences.LOGIN_DATA
import com.app.edtech.preferences.Preferences
import com.app.edtech.ui.adapter.HomeBannerAdapter
import com.app.edtech.ui.adapter.SearchInstituteAdapter
import com.app.edtech.ui.view_model.SearchInstituteViewModel
import com.app.edtech.utils.CommonUtils
import com.app.edtech.utils.CommonUtils.updateModeUI
import com.app.edtech.utils.CommonUtils.updateSortUI
import com.app.edtech.utils.network_utils.ProcessDialog
import com.app.edtech.utils.network_utils.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchInstituteFragment : BaseFragment<FragmentSearchInstituteBinding>() {
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

    override fun initView(savedInstanceState: Bundle?) {
        setupRecyclerView()

        currentPage = 1
        isLastPage = false
        instituteList.clear()

        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(
            requireContext(),
            LOGIN_DATA
        )?.data?.accessToken

        viewModel.hitBannerDataApi("Bearer $accessToken")
        callInstituteApi()
    }

    private fun setupRecyclerView() {
        homeBannerAdapter = HomeBannerAdapter(listOf())
        binding.bannerRecycler.apply {
            adapter = homeBannerAdapter
        }
    }
    private fun setupInstitutesRecycler(institutes: List<InstituteListResponse.Institute>) {

        if (!::searchInstituteAdapter.isInitialized) {
            searchInstituteAdapter = SearchInstituteAdapter(instituteList, ::onInstituteSelected)

            binding.instituteRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = searchInstituteAdapter
            }

            addPaginationScroll()
        }

        val start = instituteList.size
        instituteList.addAll(institutes)
        searchInstituteAdapter.notifyItemRangeInserted(start, institutes.size)

        isLoading = false

        // 👇 Detect last page
        if (institutes.size < pageSize) {
            isLastPage = true
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
    private fun onInstituteSelected() {
        findNavController().navigate(R.id.instituteDetailsFragment)
    }

    private fun clickEvent() {

        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchField.text.toString().trim()
//                viewModel.search(query)
                true
            } else false
        }
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
            order_type = orderType,     // ✅ dynamic
            mode = selectedMode,        // ✅ dynamic
            page = currentPage.toString(),
            limit = pageSize.toString()
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
        viewModel.getInstitutesLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "Login success: ${Gson().toJson(it)}")
                    if (it.data?.status == "true") {
                        setupInstitutesRecycler(it.data.institutes)
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
        viewModel.getCitiesLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "Login success: ${Gson().toJson(it)}")
                    if (it.data?.status == "true") {

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
        viewModel.getInstitutesLiveData().value?.data?.institutes?.let {
            setupInstitutesRecycler(it)
        }
    }

}