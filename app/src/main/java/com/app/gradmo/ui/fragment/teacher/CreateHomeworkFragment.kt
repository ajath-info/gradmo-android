package com.app.gradmo.ui.fragment.teacher

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentBatchDetailBinding
import com.app.gradmo.databinding.FragmentCreateHomeworkBinding
import com.app.gradmo.model.institute_detail.response.InstituteDetailResponse
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.view_model.BatchDetailsViewModel
import com.app.gradmo.ui.view_model.CreateHomeWorkViewModel
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class CreateHomeworkFragment : BaseFragment<FragmentCreateHomeworkBinding>(){
    private val viewModel: CreateHomeWorkViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.accessToken
//        viewModel.hitCreateHomeWorkApi("Bearer $accessToken", )
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        clickEvent()
    }

    private fun clickEvent() {

    }

    private fun setObserver() {
        viewModel.getCreateHomeWorkLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "getBatchDetailsLiveData success: ${Gson().toJson(it)}")
                    if (it.data?.status == true) {
                        Log.i("TAG", "getBatchDetailsLiveData: "+ Gson().toJson(it.data))
//                        batch = it.data.batch_details
//                        setupUI()
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "getBatchDetailsLiveData Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
    }
    override fun getLayoutId(): Int = R.layout.fragment_create_homework

    override fun restoreView() {
//        setupUI()
//        setupRecyclerView()
    }

}