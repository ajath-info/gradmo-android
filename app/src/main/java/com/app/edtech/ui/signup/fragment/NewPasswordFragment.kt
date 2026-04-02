package com.app.edtech.ui.signup.fragment

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.app.edtech.R
import com.app.edtech.databinding.FragmentNewPasswordBinding
import com.app.edtech.preferences.Preferences
import com.app.edtech.preferences.USER_TYPE
import com.app.edtech.ui.signup.view_model.ResetPasswordViewModel
import com.app.edtech.utils.CommonUtils
import com.app.edtech.utils.network_utils.ProcessDialog
import com.app.edtech.utils.network_utils.Status
import com.app.edtech.ui.signup.model.ResetPasswordRequest
import com.google.gson.Gson

class NewPasswordFragment : Fragment() {
    private lateinit var binding:FragmentNewPasswordBinding
    private var isPassHidden = true
    private var isCnfPassHidden = true
    private val resetPasswordViewModel: ResetPasswordViewModel by viewModels()
    var from = ""
    var phone = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from = it.getString("from").toString()
            phone = it.getString("phone").toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CommonUtils.hideKeyboard(requireActivity())
        setObserver()

    }

    private fun setObserver() {
        resetPasswordViewModel.getResetPasswordLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "interest list success: ${Gson().toJson(it)}")
                    if (it.data?.status=="true"){
                            Toast.makeText(requireContext(), it.data.msg, Toast.LENGTH_SHORT).show()
                            findNavController().navigate(
                                R.id.signinFragment,
                                null,
                                NavOptions.Builder()
                                    .setPopUpTo(R.id.signup_flow_nav, true) // clears everything in backstack
                                    .build()
                            )
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewPasswordBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }
    private fun initViews(){
        setPasswordToggle()
        binding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            btnChangePassword.setOnClickListener {
                checkValidation()
            }

        }
    }

    private fun checkValidation() {
        val password = binding.password.text.trim().toString()
        val cnfPassword = binding.etCnfpassword.text.trim().toString()
        val userType = Preferences.getStringPreference(requireActivity(), USER_TYPE)

        if(password.isEmpty()){
            Toast.makeText(requireActivity(), "Please enter new password", Toast.LENGTH_SHORT).show()
        }else if(cnfPassword.isEmpty()){
            Toast.makeText(requireActivity(), "Please enter confirm new password", Toast.LENGTH_SHORT).show()
        }else if(password!=cnfPassword){
            Toast.makeText(requireActivity(), "New password and confirm new password not matched", Toast.LENGTH_SHORT).show()
        }else{
            //Api hitting
            val model = ResetPasswordRequest(
                mobile = phone,
                password = password,
                confirm_password = cnfPassword,
                user_type = userType
            )
            resetPasswordViewModel.hitResetPassword(model)

//            findNavController().navigate(
//                R.id.signinFragment,
//                null,
//                NavOptions.Builder()
//                    .setPopUpTo(R.id.signup_flow_nav, true) // clears everything in backstack
//                    .build()
//            )
        }
    }

    private fun setPasswordToggle() {
        //for new Password
        binding.password.transformationMethod = CommonUtils.DotPasswordTransformationMethod
        binding.passwordToggle.setOnClickListener {
            isPassHidden = if (isPassHidden) {
                binding.passwordToggle.setImageResource(R.drawable.open_eye_2)
                binding.password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                false
            } else {
                binding.passwordToggle.setImageResource(R.drawable.close_eye_2)
                binding.password.transformationMethod = CommonUtils.DotPasswordTransformationMethod
                true
            }
            binding.password.setSelection(binding.password.text.toString().length)
        }


        //for confirm new password
        binding.etCnfpassword.transformationMethod = CommonUtils.DotPasswordTransformationMethod
        binding.cnfPwdToggle.setOnClickListener {
            isCnfPassHidden = if (isCnfPassHidden) {
                binding.cnfPwdToggle.setImageResource(R.drawable.open_eye_2)
                binding.etCnfpassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                false
            } else {
                binding.cnfPwdToggle.setImageResource(R.drawable.close_eye_2)
                binding.etCnfpassword.transformationMethod = CommonUtils.DotPasswordTransformationMethod
                true
            }
            binding.etCnfpassword.setSelection(binding.etCnfpassword.text.toString().length)
        }


    }
}