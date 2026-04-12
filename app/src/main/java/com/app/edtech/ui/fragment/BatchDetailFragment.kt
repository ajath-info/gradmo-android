package com.app.edtech.ui.fragment

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentBatchDetailBinding
import com.app.edtech.model.static.StaticLists
import com.app.edtech.ui.adapter.AdapterBatchDetailsItems
import kotlinx.coroutines.launch

class BatchDetailFragment : BaseFragment<FragmentBatchDetailBinding>() {
    private lateinit var adapterBatchDetailsItems: AdapterBatchDetailsItems  // replace with your adapter

    override fun initView(savedInstanceState: Bundle?) {
        setupRecyclerView()
        clickEvent()
        observeViewModel()
    }
    private fun setupRecyclerView() {
        adapterBatchDetailsItems = AdapterBatchDetailsItems(StaticLists.batchDetailList, ::onBatchSelected)
        binding.recyclerBatchDetailItem.apply {
            adapter = adapterBatchDetailsItems
        }
    }
    fun onBatchSelected(){
        findNavController().navigate(R.id.batchDetailFragment)
    }

    private fun clickEvent() {

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