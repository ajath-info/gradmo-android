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

        val request = InstitutesListRequest(
            latitude = "28.93466857138595",
            longitude = "78.34283781396569",
            order_field = "name",
            order_type = "DESC",
            page = currentPage.toString(),
            limit = pageSize.toString()
        )

        viewModel.hitInstitutesDataApi("Bearer $accessToken", request)
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

        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(
            requireContext(),
            LOGIN_DATA
        )?.data?.accessToken

        val request = InstitutesListRequest(
            latitude = "28.93466857138595", // later replace with dynamic
            longitude = "78.34283781396569",
            order_field = "name",
            order_type = "DESC",
            page = currentPage.toString(),
            limit = pageSize.toString()
        )

        viewModel.hitInstitutesDataApi("Bearer $accessToken", request)
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

        // Inflate using binding
        val sheetBinding = FragmentFilterInstituteBottomSheetBinding.inflate(layoutInflater)

        dialog.setContentView(sheetBinding.root)

        // OPTIONAL: make background transparent so rounded corners show
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }

        // Handle clicks
        sheetBinding.closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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
    }
    override fun restoreView() {
        viewModel.getInstitutesLiveData().value?.data?.institutes?.let {
            setupInstitutesRecycler(it)
        }
    }

}