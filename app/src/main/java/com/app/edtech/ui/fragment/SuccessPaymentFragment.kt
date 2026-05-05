package com.app.edtech.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentPaymentSummaryBinding
import com.app.edtech.databinding.FragmentSuccessPaymentBinding
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
    }
    override fun getLayoutId(): Int = R.layout.fragment_success_payment
    private fun setObserver() {

    }
    override fun restoreView() {

    }

}