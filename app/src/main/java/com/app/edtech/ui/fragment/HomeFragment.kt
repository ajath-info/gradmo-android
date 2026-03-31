package com.app.edtech.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentHomeBinding
import com.app.edtech.ui.activity.HomeActivity
import com.app.edtech.ui.view_model.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val viewModel: HomeViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
        clickEvent()
    }

    private fun clickEvent() {
        binding.sideMenu.setOnClickListener {
            (activity as? HomeActivity)?.openDrawer()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }
}