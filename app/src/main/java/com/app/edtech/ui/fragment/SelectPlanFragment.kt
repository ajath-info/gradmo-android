package com.app.edtech.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentFilterInstituteBottomSheetBinding
import com.app.edtech.databinding.FragmentSearchInstituteBinding
import com.app.edtech.databinding.FragmentSelectPlanBinding
import com.app.edtech.model.institute_list.request.InstitutesListRequest
import com.app.edtech.model.institute_list.response.InstituteListResponse
import com.app.edtech.model.login.response.LoginResponse
import com.app.edtech.preferences.LOGIN_DATA
import com.app.edtech.preferences.Preferences
import com.app.edtech.ui.activity.HomeActivity
import com.app.edtech.ui.adapter.HomeBannerAdapter
import com.app.edtech.ui.adapter.SearchInstituteAdapter
import com.app.edtech.ui.view_model.SearchInstituteViewModel
import com.app.edtech.ui.view_model.SelectPlanViewModel
import com.app.edtech.utils.CommonUtils
import com.app.edtech.utils.CommonUtils.updateModeUI
import com.app.edtech.utils.CommonUtils.updateSortUI
import com.app.edtech.utils.network_utils.ProcessDialog
import com.app.edtech.utils.network_utils.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectPlanFragment : BaseFragment<FragmentSelectPlanBinding>() {
    private val viewModel: SelectPlanViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.accessToken
        viewModel.hitPlanDetailApi("Bearer $accessToken", "1")
    }

    private fun clickEvent() {
        binding.backButton.setOnClickListener{
            findNavController().popBackStack()
        }
        binding.continueButton.setOnClickListener {
            findNavController().navigate(R.id.paymentSummaryFragment)
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

                    } else {
                        Toast.makeText(requireContext(), "${it.data?.message}", Toast.LENGTH_SHORT).show()
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
        viewModel.getPlanDetailLiveData().value?.data?.data?.let {
//            setupInstitutesRecycler(it, true, totalRecords)
        }
    }

}