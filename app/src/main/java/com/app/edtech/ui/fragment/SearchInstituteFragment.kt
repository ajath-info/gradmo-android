package com.app.edtech.ui.fragment

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentSearchInstituteBinding
import com.app.edtech.ui.adapter.HomeBannerAdapter
import com.app.edtech.ui.adapter.SearchInstituteAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchInstituteFragment : BaseFragment<FragmentSearchInstituteBinding>() {

    private lateinit var searchInstituteAdapter: SearchInstituteAdapter  // replace with your adapter
    private lateinit var homeBannerAdapter: HomeBannerAdapter  // replace with your adapter

    override fun initView(savedInstanceState: Bundle?) {
        setupRecyclerView()
        clickEvent()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        searchInstituteAdapter = SearchInstituteAdapter(::onInstituteSelected)
        binding.instituteRecycler.apply {
            adapter = searchInstituteAdapter
        }


        homeBannerAdapter = HomeBannerAdapter()
        binding.bannerRecycler.apply {
            adapter = homeBannerAdapter
        }
    }
    private fun onInstituteSelected(){
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
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.categories.collect { list ->
//                categoryAdapter.submitList(list)
//            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_search_institute
}