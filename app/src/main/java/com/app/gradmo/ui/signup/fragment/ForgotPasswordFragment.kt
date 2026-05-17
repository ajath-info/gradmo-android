package com.app.gradmo.ui.signup.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.gradmo.R
import com.app.gradmo.databinding.FragmentEmailBinding
import com.app.gradmo.model.login.request.LoginRequest
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.preferences.USER_TYPE
import com.app.gradmo.ui.signup.view_model.ForgotPasswordViewModel
import com.app.gradmo.utils.CommonUtils
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.google.gson.Gson

class ForgotPasswordFragment : Fragment() {
    private lateinit var binding:FragmentEmailBinding
    var from = ""
    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from = it.getString("from").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmailBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            btnResetPassword.setOnClickListener {
                checkValidation()
            }
            clMain.setOnClickListener {
                CommonUtils.hideKeyboard(requireActivity())
            }
        }
    }

    private fun checkValidation() {
        val etPhone = binding.phoneInput.text.toString()
        if(etPhone.isEmpty()){
            Toast.makeText(requireActivity(), "Please enter your number", Toast.LENGTH_SHORT).show()
        }else if(etPhone.length<10){
            Toast.makeText(requireActivity(), "Please enter valid number", Toast.LENGTH_SHORT).show()
        }else{
            val userType = Preferences.getStringPreference(requireActivity(), USER_TYPE)
            val request = LoginRequest(mobile = etPhone, user_type = userType)
            viewModel.hitForgotPasswordSendOtp(request)
        }
    }

    private fun initObserver() {
        viewModel.getForgotPasswordSendOtpLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "resend otp: ${Gson().toJson(it)}")
                    if (it.data?.status=="true"){
                        Toast.makeText(requireContext(), it.data.msg, Toast.LENGTH_SHORT).show()
                        val bundle = Bundle()
                        bundle.putString("phone",binding.phoneInput.text.toString())
                        bundle.putString("from","forgot")
                        findNavController().navigate(R.id.otpFragment,bundle)
                    }else{
                        Toast.makeText(requireContext(), "${it.data?.msg}", Toast.LENGTH_SHORT).show()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CommonUtils.touchHideKeyBoard(view,requireActivity())
        initObserver()
    }
}