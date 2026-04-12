package com.app.edtech.ui.fragment

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentInstituteDetailsBinding
import com.app.edtech.ui.adapter.AdapterInstituteBatch
import com.app.edtech.ui.adapter.AdapterInstituteRating
import kotlinx.coroutines.launch

class InstituteDetailsFragment : BaseFragment<FragmentInstituteDetailsBinding>() {
    private lateinit var adapterInstituteBatch: AdapterInstituteBatch  // replace with your adapter
    private lateinit var adapterInstituteRating: AdapterInstituteRating  // replace with your adapter

    override fun initView(savedInstanceState: Bundle?) {
        setupRecyclerView()
        clickEvent()
        observeViewModel()
    }
    private fun setupRecyclerView() {
        adapterInstituteBatch = AdapterInstituteBatch(::onBatchSelected)
        binding.categoryRecyclerView.apply {
            adapter = adapterInstituteBatch          // attach your adapter here
        }

        adapterInstituteRating = AdapterInstituteRating(::onRatingSelected)
        binding.ratingReviewRecycler.apply {
            adapter = adapterInstituteRating          // attach your adapter here
        }
    }
    fun onBatchSelected(){
        findNavController().navigate(R.id.batchDetailFragment)
    }
    fun onRatingSelected(){

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

    override fun getLayoutId(): Int = R.layout.fragment_institute_details
}