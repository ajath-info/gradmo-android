package com.app.edtech.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentBatchListBinding
import com.app.edtech.databinding.FragmentInstituteDetailsBinding
import com.app.edtech.model.institute_detail.request.InstituteDetailRequest
import com.app.edtech.model.institute_detail.response.InstituteDetailResponse
import com.app.edtech.model.institute_list.response.InstituteListResponse
import com.app.edtech.model.login.response.LoginResponse
import com.app.edtech.preferences.LOGIN_DATA
import com.app.edtech.preferences.Preferences
import com.app.edtech.ui.adapter.AdapterInstituteBatch
import com.app.edtech.ui.adapter.AdapterInstituteRating
import com.app.edtech.ui.view_model.InstituteDetailsViewModel
import com.app.edtech.utils.network_utils.ProcessDialog
import com.app.edtech.utils.network_utils.Status
import com.bumptech.glide.Glide
import com.google.gson.Gson

class BatchListFragment : BaseFragment<FragmentBatchListBinding>() {
    private lateinit var adapterInstituteBatch: AdapterInstituteBatch  // replace with your adapter
    private lateinit var adapterInstituteRating: AdapterInstituteRating  // replace with your adapter

    private val viewModel: InstituteDetailsViewModel by viewModels()

    private var institute: InstituteListResponse.Institute = InstituteListResponse.Institute()


    override fun initView(savedInstanceState: Bundle?) {
        institute = arguments?.getParcelable("institute") ?: InstituteListResponse.Institute()
        Log.i("TAG", "institute: " + Gson().toJson(institute))
        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.accessToken
        viewModel.hitInstitutesDataApi("Bearer $accessToken", InstituteDetailRequest("69"))
        setupUI()
    }

    private fun setupUI() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickEvent()
        observeViewModel()
    }
    private fun setupBatchRecycler(batches: List<InstituteDetailResponse.Batche>) {
        adapterInstituteBatch = AdapterInstituteBatch(batches, ::onBatchSelected)
        binding.batchRecycler.apply {
            adapter = adapterInstituteBatch          // attach your adapter here
        }
    }

    fun onBatchSelected(batch:InstituteDetailResponse.Batche){
        findNavController().navigate(R.id.batchDetailFragment)
    }

    private fun clickEvent() {
        binding.backButton.setOnClickListener{
            findNavController().popBackStack()
        }
        binding.backButton.setOnClickListener{
            findNavController().navigate(R.id.batchListFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.getInstitutesLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "Login success: ${Gson().toJson(it)}")
                    if (it.data?.status == "true") {
                        setupBatchRecycler(it.data.batches)
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
        viewModel.getInstitutesLiveData().value?.data?.batches?.let {
            setupBatchRecycler(it)
        }
    }
    override fun getLayoutId(): Int = R.layout.fragment_batch_list
}