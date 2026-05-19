package com.app.gradmo.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentSuccessPaymentBinding
import com.app.gradmo.databinding.FragmentTestCompletedBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestCompletedFragment : BaseFragment<FragmentTestCompletedBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        setupUI()
    }

    private fun setupUI() {

    }
    private fun clickEvent() {
        binding.backToAssessment.setOnClickListener {
            findNavController().popBackStack(
                R.id.batchDetailFragment,
                false
            )
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        clickEvent()
        handleBackPress()
    }
    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    findNavController().popBackStack(
                        R.id.batchDetailFragment,
                        false
                    )
                }
            }
        )
    }
    override fun getLayoutId(): Int = R.layout.fragment_test_completed
    private fun setObserver() {

    }
    override fun restoreView() {

    }

}