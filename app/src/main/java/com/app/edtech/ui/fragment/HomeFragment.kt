package com.app.edtech.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentHomeBinding
import com.app.edtech.ui.activity.HomeActivity
import com.app.edtech.ui.adapter.HomeInstituteAdapter
import com.app.edtech.ui.view_model.HomeViewModel
import com.app.edtech.utils.CommonUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var homeInstituteAdapter: HomeInstituteAdapter  // replace with your adapter

    override fun initView(savedInstanceState: Bundle?) {
        setupRecyclerView()
        clickEvent()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        homeInstituteAdapter = HomeInstituteAdapter()
        binding.categoryRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = homeInstituteAdapter          // attach your adapter here
            addItemDecoration(
                CommonUtils.ItemSpacingDecoration(spacing = 16)   // optional spacing helper below
            )
        }
    }

    private fun clickEvent() {
        binding.sideMenu.setOnClickListener {
            (activity as? HomeActivity)?.openDrawer()
        }

        binding.seeAllLayout.setOnClickListener {
            // navigate to full list screen
        }

        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchField.text.toString().trim()
                viewModel.search(query)
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

    override fun getLayoutId(): Int = R.layout.fragment_home
}