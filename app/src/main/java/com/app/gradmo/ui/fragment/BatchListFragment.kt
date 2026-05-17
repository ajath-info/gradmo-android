package com.app.gradmo.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentBatchListBinding
import com.app.gradmo.model.institute_detail.request.InstituteDetailRequest
import com.app.gradmo.model.institute_detail.response.InstituteDetailResponse
import com.app.gradmo.model.institute_list.response.InstituteListResponse
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.adapter.AdapterInstituteBatch
import com.app.gradmo.ui.adapter.AdapterInstituteRating
import com.app.gradmo.ui.view_model.InstituteDetailsViewModel
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.google.gson.Gson

class BatchListFragment : BaseFragment<FragmentBatchListBinding>() {
    private lateinit var adapterInstituteBatch: AdapterInstituteBatch  // replace with your adapter
    private lateinit var adapterInstituteRating: AdapterInstituteRating  // replace with your adapter

    private val viewModel: InstituteDetailsViewModel by viewModels()

    private var institute: InstituteListResponse.Institute = InstituteListResponse.Institute()


    override fun initView(savedInstanceState: Bundle?) {
        institute = arguments?.getParcelable("institute") ?: InstituteListResponse.Institute()
        Log.i("TAG", "institute in batchlist: " + Gson().toJson(institute))
        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.accessToken
        viewModel.hitInstitutesDataApi("Bearer $accessToken", InstituteDetailRequest(institute.instituteId.toString()))
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
        val bundle=Bundle()
        bundle.putParcelable("batch", batch)
        findNavController().navigate(R.id.batchDetailFragment, bundle)
    }

    private fun clickEvent() {
        binding.backButton.setOnClickListener{
            findNavController().popBackStack()
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