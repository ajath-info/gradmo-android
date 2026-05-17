package com.app.gradmo.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentSuccessPaymentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuccessPaymentFragment : BaseFragment<FragmentSuccessPaymentBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        setupUI()
    }

    private fun setupUI() {

    }
    private fun clickEvent() {
        binding.paymentButton.setOnClickListener {
            findNavController().navigate(
                R.id.homeFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.homeFragment, false).build())
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
                    val intent = requireActivity().intent
                    requireActivity().finish()
                    startActivity(intent)
                }
            }
        )
    }
    override fun getLayoutId(): Int = R.layout.fragment_success_payment
    private fun setObserver() {

    }
    override fun restoreView() {

    }

}