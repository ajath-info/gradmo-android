package com.app.edtech.ui.get_started.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentGetStartedBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GetStartedFragment : BaseFragment<FragmentGetStartedBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        onClick()
    }

    private fun onClick() {
        binding.apply {
            registerAsStudent.setOnClickListener {
                findNavController().navigate(R.id.joinFragment)
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_get_started
    }

}