package com.app.gradmo.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentMyBatchBinding
import com.app.gradmo.databinding.FragmentPaymentHistoryBinding
import com.app.gradmo.model.batch_list.BatchListRequest
import com.app.gradmo.model.batch_list.BatchListResponse.Data.EnrolledBatche
import com.app.gradmo.model.institute_detail.response.InstituteDetailResponse
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.model.payment_history.PaymentHistoryRequest
import com.app.gradmo.model.payment_history.PaymentHistoryResponse.PaymentData
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.adapter.AdapterMyBatchList
import com.app.gradmo.ui.adapter.AdapterPaymentHistory
import com.app.gradmo.ui.view_model.MyBatchViewModel
import com.app.gradmo.ui.view_model.PaymentHistoryViewModel
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class PaymentHistoryFragment : BaseFragment<FragmentPaymentHistoryBinding>() {
    private lateinit var adapterInstituteBatch: AdapterPaymentHistory  // replace with your adapter

    private val viewModel: PaymentHistoryViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
//        institute = arguments?.getParcelable("institute") ?: InstituteListResponse.Institute()
//        Log.i("TAG", "my batchlist: " + Gson().toJson(institute))
        val userData = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data
        val accessToken = userData?.accessToken
        viewModel.hitPaymentHistoryApi("Bearer $accessToken", PaymentHistoryRequest(userData?.studentId.toString()))
        setupUI()
    }

    private fun setupUI() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickEvent()
        observeViewModel()
    }
    private fun setupBatchRecycler(batches: List<PaymentData>) {
        adapterInstituteBatch = AdapterPaymentHistory(batches, ::onBatchSelected)
        binding.batchRecycler.apply {
            adapter = adapterInstituteBatch          // attach your adapter here
        }
    }

    fun onBatchSelected(batch:PaymentData){
        val bundle=Bundle()
//        bundle.putParcelable("batch", batch.toBatche())
//        bundle.putString("instituteName", institute.name)
//        findNavController().navigate(R.id.batchDetailFragment, bundle)
    }
    fun EnrolledBatche.toBatche(): InstituteDetailResponse.Batche {
        return InstituteDetailResponse.Batche(
            admin_id = null,
            batch_image = batchImage,
            batch_mode = null,
            batch_name = batchName,
            batch_offer_price = null,
            batch_price = null,
            batch_type = batch_type?.toString(),
            cat_id = null,
            description = description,
            end_date = end_date,
            end_time = end_time,
            id = batch_id?.toString(),
            institute_id = null,
            no_of_student = null,
            pay_mode = null,
            start_date = start_date,
            start_time = start_time,
            status = enrollment_status?.toString(),
            sub_cat_id = null
        )
    }
    private fun clickEvent() {
        binding.backButton.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        viewModel.getPaymentHistoryLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "Login success: ${Gson().toJson(it)}")
                    if (it.data?.status == "true") {
                        setupBatchRecycler(it.data.paymentData ?: listOf())
//                        setupRatingRecycler(it.data.rating)
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.msg}", Toast.LENGTH_SHORT)
                            .show()
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Login Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
    }

    override fun restoreView() {
        setupUI()
        viewModel.getPaymentHistoryLiveData().value?.data?.paymentData?.let {
            setupBatchRecycler(it)
        }
    }
    override fun getLayoutId(): Int = R.layout.fragment_payment_history
}