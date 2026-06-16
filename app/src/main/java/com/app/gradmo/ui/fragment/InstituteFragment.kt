package com.app.gradmo.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentInstituteBinding
import com.app.gradmo.model.banner.response.BannerResponse
import com.app.gradmo.model.institute_detail.response.InstituteDetailResponse
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.model.user_detail.UserDetailResponse
import com.app.gradmo.model.user_detail.UserDetailResponse.Data.Batch
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.activity.HomeActivity
import com.app.gradmo.ui.adapter.HomeBannerAdapter
import com.app.gradmo.ui.adapter.InstitueTabAdapter
import com.app.gradmo.ui.view_model.InstituteViewModel
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InstituteFragment() : BaseFragment<FragmentInstituteBinding>() {

    private val viewModel: InstituteViewModel by viewModels()
    private var flow = ""

    private var searchRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())
    private var searchQuery: String = ""

//    private val autoScrollHandler = Handler(Looper.getMainLooper())
//    private val AUTO_SCROLL_DELAY = 7000L
    private lateinit var homeBannerAdapter: HomeBannerAdapter
    lateinit var adapter: InstitueTabAdapter
    var data = UserDetailResponse.Data()
    var batchMap : Map<Int, List<UserDetailResponse.Data.Batch>> = mapOf()

    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            val itemCount = homeBannerAdapter.itemCount
            if (itemCount == 0) return
            val nextItem = binding.bannerViewPager.currentItem + 1
            binding.bannerViewPager.setCurrentItem(nextItem, true)
//            autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
//        flow = arguments?.getString("flow", "") ?: ""
        setupUI()
        callInstituteApi()
    }

    private fun setupUI() {
        /*if (flow == "seeAll") {
            binding.title.text = "Institutes"
            (activity as HomeActivity).hideNavigationView()
        }*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        clickEvent()
    }

    private fun setObserver() {
        viewModel.getInstitutesLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog(true)
                    data = it.data?.data ?: return@observe

                    // Group batches by institute_id
                    batchMap = data.batchList.groupBy { batch -> batch.institute_id }

                    setupInstituteRecycler()
                }

                Status.LOADING -> ProcessDialog.showDialog(requireContext(), true)

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
    }

    private fun setupInstituteRecycler() {
        adapter = InstitueTabAdapter(
            institutes = data.instituteList ?: listOf(),
            batchMap = batchMap,
            onInstituteSelected = { institute ->
//                            val bundle = Bundle().apply {
//                                putParcelable("institute", institute)
//                            }
//                            findNavController().navigate(R.id.instituteDetailsFragment, bundle)
            },
            onBatchSelected = { batch ->
                val bundle=Bundle()
                bundle.putParcelable("batch", batch.toBatche())
//        bundle.putString("instituteName", institute.name)
                findNavController().navigate(R.id.batchDetailFragment, bundle)                        }
        )
        binding.instituteRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@InstituteFragment.adapter
        }
        if (data.instituteList?.size==0){
            binding.noDataPlaceholder.isVisible = true
            binding.instituteRecycler.isVisible = false
        }else{
            binding.noDataPlaceholder.isVisible = false
            binding.instituteRecycler.isVisible = true
        }
    }

    fun Batch.toBatche(): InstituteDetailResponse.Batche {
        return InstituteDetailResponse.Batche(
            admin_id = null,
            batch_image = batchImage,
            batch_mode = null,
            batch_name = batchName,
            batch_offer_price = null,
            batch_price = null,
            batch_type = batch_type.toString(),
            cat_id = null,
            description = description,
            end_date = end_date,
            end_time = end_time,
            id = batch_id.toString(),
            institute_id = institute_id.toString(),
            no_of_student = null,
            pay_mode = null,
            start_date = start_date,
            start_time = start_time,
            status = enrollment_status.toString(),
            sub_cat_id = null
        )
    }
    private fun callInstituteApi() {
        val token = Preferences.getCustomModelPreference<LoginResponse>(
            requireContext(), LOGIN_DATA
        )?.data?.accessToken
        viewModel.hitInstitutesDataApi("Bearer $token")
    }

    private fun clickEvent() {
        binding.backButton.setOnClickListener {
            (activity as HomeActivity).popBackToHome()
        }

        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchQuery = binding.searchField.text.toString().trim()
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
                        callInstituteApi()
                    }
                }
                handler.postDelayed(searchRunnable!!, 500)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

//        binding.filterLayout.setOnClickListener { showBottomSheet() }
    }

    // ... keep showBottomSheet() as-is, it's independent

    // Banner methods unchanged
    fun setUpBannerViewPager(banners: List<BannerResponse.Data.Banner>) { /* same as before */ }

//    override fun onPause() { super.onPause(); autoScrollHandler.removeCallbacks(autoScrollRunnable) }
//    override fun onResume() { super.onResume(); if (::homeBannerAdapter.isInitialized) autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY) }
//    override fun onDestroyView() { super.onDestroyView(); autoScrollHandler.removeCallbacks(autoScrollRunnable) }

    override fun getLayoutId(): Int = R.layout.fragment_institute
    override fun restoreView() {
        viewModel.getInstitutesLiveData().value?.data?.data?.let {
            setupInstituteRecycler()
        }
    }
}