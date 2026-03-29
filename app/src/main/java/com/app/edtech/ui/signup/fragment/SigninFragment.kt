package com.app.edtech.ui.signup.fragment

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentSigninBinding
import com.app.edtech.model.login.request.LoginRequest
import com.app.edtech.model.login.response.LoginResponse
import com.app.edtech.preferences.FCM_TOKEN
import com.app.edtech.preferences.IS_LOGIN
import com.app.edtech.preferences.LOGIN_DATA
import com.app.edtech.preferences.Preferences
import com.app.edtech.preferences.UserPreference
import com.app.edtech.ui.activity.HomeActivity
import com.app.edtech.ui.signup.view_model.SigninViewModel
import com.app.edtech.utils.CommonUtils
import com.app.edtech.utils.network_utils.ProcessDialog
import com.app.edtech.utils.network_utils.Status
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SigninFragment : BaseFragment<FragmentSigninBinding>() {
    private var isPassHidden = true
    private val viewModel: SigninViewModel by viewModels()


    override fun initView(savedInstanceState: Bundle?) {
        setPasswordToggle()
        onClick()
//        binding.passwordToggle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), PorterDuff.Mode.SRC_IN)
//        clickTermsConditions()
//        clickDontHaveAccount()
    }
//    private fun clickDontHaveAccount(){
//        val fullText = "Don’t have an account ?  Sign Up"
//        val spannableString = SpannableString(fullText)
//        val signUpClick = object : ClickableSpan() {
//            override fun onClick(widget: View) {
//                findNavController().navigate(R.id.registrationFragment)
//            }
//
//            override fun updateDrawState(ds: TextPaint) {
//                super.updateDrawState(ds)
//                ds.color = ContextCompat.getColor(requireActivity(),R.color.theme) // your link color
//                ds.isUnderlineText = false
//            }
//        }
//
//        val signUpStart = fullText.indexOf("Sign Up")
//        val signUpEnd = signUpStart + "Sign Up".length
//
//        spannableString.setSpan(signUpClick, signUpStart, signUpEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        binding.tvDontHaveAccount.text = spannableString
//        binding.tvDontHaveAccount.movementMethod = LinkMovementMethod.getInstance()
//        binding.tvDontHaveAccount.highlightColor = Color.TRANSPARENT
//
//    }
//    private fun clickTermsConditions(){
//        val fullText = "I agree to Terms & Conditions and Privacy Policy of the App"
//        val spannableString = SpannableString(fullText)
//        val termsClickable = object : ClickableSpan() {
//            override fun onClick(widget: View) {
//                // Handle Terms & Conditions click
//                //Toast.makeText(widget.context, "Terms & Conditions clicked", Toast.LENGTH_SHORT).show()
//                val bundle = Bundle()
//                bundle.putString("screen","termsCondition")
//                findNavController().navigate(R.id.termsConditionsFragment,bundle)
//            }
//
//            override fun updateDrawState(ds: TextPaint) {
//                super.updateDrawState(ds)
//                ds.color = ContextCompat.getColor(requireActivity(),R.color.theme) // your link color
//                ds.isUnderlineText = false
//            }
//        }
//
//        // Privacy Policy click span
//        val privacyClickable = object : ClickableSpan() {
//            override fun onClick(widget: View) {
//                // Handle Privacy Policy click
//                //Toast.makeText(widget.context, "Privacy Policy clicked", Toast.LENGTH_SHORT).show()
//                val bundle = Bundle()
//                bundle.putString("screen","privacy")
//                findNavController().navigate(R.id.termsConditionsFragment,bundle)
//            }
//
//            override fun updateDrawState(ds: TextPaint) {
//                super.updateDrawState(ds)
//                ds.color = ContextCompat.getColor(requireActivity(),R.color.theme)
//                ds.isUnderlineText = false
//            }
//        }
//
//        // Apply spans
//        val termsStart = fullText.indexOf("Terms & Conditions")
//        val termsEnd = termsStart + "Terms & Conditions".length
//
//        val privacyStart = fullText.indexOf("Privacy Policy")
//        val privacyEnd = privacyStart + " Privacy Policy".length
//
//        spannableString.setSpan(termsClickable, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        spannableString.setSpan(privacyClickable, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        binding.tvTermsConditions.text = spannableString
//        binding.tvTermsConditions.movementMethod = LinkMovementMethod.getInstance()
//        binding.tvTermsConditions.highlightColor = Color.TRANSPARENT
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        CommonUtils.touchHideKeyBoard(view,requireActivity())
        setObserver()
    }
    private fun setObserver() {
        viewModel.getLoginLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "Login success: ${Gson().toJson(it)}")
                    if (it.data?.status=="true"){
                        if (it.data.data?.is_profile_completed==1){
                            Preferences.setStringPreference(requireContext(), IS_LOGIN, "2")
                            Preferences.setCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA, it.data)
                            CommonUtils.hideKeyboard(requireActivity())
                            Log.i("TAG", "setObserver: "+Preferences.getStringPreference(requireContext(), FCM_TOKEN))
                            startActivity(Intent(requireActivity(), HomeActivity::class.java))
                            requireActivity().finish()
                        }else{
                            Preferences.setStringPreference(requireContext(), IS_LOGIN, "2")
                            Preferences.setCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA, it.data)
                            UserPreference.loginRequest = LoginRequest(name = it.data.data?.name, mobile = it.data.data?.mobile, email = it.data.data?.email)
                            UserPreference.studentId = it.data.data?.studentId.toString()
                            val bundle = Bundle()
                            bundle.putString("from","signup")
                            findNavController().navigate(R.id.editProfileNewFragment,bundle)
                        }
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
        viewModel.getSendLoginOtpLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "Login success: ${Gson().toJson(it)}")
                    if (it.data?.status=="true"){
                        CommonUtils.hideKeyboard(requireActivity())
                        Toast.makeText(requireContext(), it.data.msg, Toast.LENGTH_SHORT).show()
                        val bundle = Bundle()
                        bundle.putString("phone",viewModel.phone.get())
                        bundle.putString("from","login")
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

        viewModel.validationMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onClick() {
        binding.apply {
            tvForgotPassword.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("from","login")
                findNavController().navigate(R.id.emailFragment,bundle)
            }
            loginViaOtpButton.setOnClickListener {
                if (viewModel?.loginType?.value.equals("password")){
                    viewModel?.loginType?.postValue("otp")
                    passwordLayout.isVisible = false
                    emailInput.isVisible = false
                    phoneInput.isVisible = true
                    loginViaOtpButton.text = getString(R.string.login_via_email)
                }else {
                    viewModel?.loginType?.postValue("password")
                    passwordLayout.isVisible = true
                    emailInput.isVisible = true
                    phoneInput.isVisible = false
                    loginViaOtpButton.text = getString(R.string.login_via_otp)
                }
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_signin
    }
    private fun setPasswordToggle() {
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
    }
}