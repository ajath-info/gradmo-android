package com.app.gradmo.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentSelectPlanBinding
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.model.plan_detail.PlanDetailsResponse
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.view_model.SelectPlanViewModel
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectPlanFragment : BaseFragment<FragmentSelectPlanBinding>() {
    private val viewModel: SelectPlanViewModel by viewModels()
    lateinit var planDetail: PlanDetailsResponse.Data
    private var batch_price = ""
    private var batch_offer_price = ""
    private var batch_id = ""

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            batch_price = it.getString("batch_price").toString()
            batch_offer_price = it.getString("batch_offer_price").toString()
            batch_id = it.getString("batch_id").toString()
        }
        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.accessToken
        viewModel.hitPlanDetailApi("Bearer $accessToken", "1")
    }

    private fun clickEvent() {
        binding.backButton.setOnClickListener{
            findNavController().popBackStack()
        }
        binding.continueButton.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("platform_fee", planDetail.plans.get(0).amount.toString())
            bundle.putString("batch_price", batch_price)
            bundle.putString("batch_offer_price", batch_offer_price)
            bundle.putString("batch_id", batch_id)
            findNavController().navigate(R.id.paymentSummaryFragment, bundle)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        clickEvent()
    }
    override fun getLayoutId(): Int = R.layout.fragment_select_plan
    private fun setObserver() {
        viewModel.getPlanDetailLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "Login success: ${Gson().toJson(it)}")
                    if (it.data?.status == "true") {
                        planDetail = it.data.data
                        setupUI()
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.message}", Toast.LENGTH_SHORT).show()
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
    }

    private fun setupUI() {
        binding.yearlyAmount.text = "₹ ${planDetail.plans.get(0).amount}"
        binding.batchAmount.text = "₹ ${batch_price}  / 12 Month"
    }

    override fun restoreView() {
        viewModel.getPlanDetailLiveData().value?.data?.data?.let {
            setupUI()
        }
    }

}