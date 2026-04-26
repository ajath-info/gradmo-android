package com.app.edtech.ui.fragment

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentBatchDetailBinding
import com.app.edtech.model.institute_detail.response.InstituteDetailResponse
import com.app.edtech.model.institute_list.response.InstituteListResponse
import com.app.edtech.model.static.StaticLists
import com.app.edtech.ui.adapter.AdapterBatchDetailsItems
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class BatchDetailFragment : BaseFragment<FragmentBatchDetailBinding>() {
    private lateinit var adapterBatchDetailsItems: AdapterBatchDetailsItems  // replace with your adapter
    private var batch: InstituteDetailResponse.Batche = InstituteDetailResponse.Batche()

    override fun initView(savedInstanceState: Bundle?) {
        batch = arguments?.getParcelable("batch") ?: InstituteDetailResponse.Batche()
        setupRecyclerView()
        setupUI()
        clickEvent()
        observeViewModel()
    }

    private fun setupUI() {
        binding.apply {
            Glide.with(root.context).load(batch.batch_image).placeholder(R.drawable.banner_placeholder).error(R.drawable.banner_placeholder).into(imageView)
            instituteName.text = batch.batch_name
            tvTeacherName.text = "N/A"
            tvTiming.text = "${batch.start_time} - ${batch.end_time}"
        }
    }

    private fun setupRecyclerView() {
        adapterBatchDetailsItems = AdapterBatchDetailsItems(StaticLists.batchDetailList, ::onBatchItemSelected)
        binding.recyclerBatchDetailItem.apply {
            adapter = adapterBatchDetailsItems
        }
    }
    fun onBatchItemSelected(){

    }

    private fun clickEvent() {
        binding.backButton.setOnClickListener{
            findNavController().popBackStack()
        }
        binding.enrollButton.setOnClickListener {
            findNavController().navigate(R.id.selectPlanFragment)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.categories.collect { list ->
//                categoryAdapter.submitList(list)
//            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_batch_detail
}