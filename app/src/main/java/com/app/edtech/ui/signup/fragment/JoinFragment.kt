package com.app.edtech.ui.signup.fragment

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import android.widget.Toast
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentJoinBinding
import com.app.edtech.databinding.FragmentSigninBinding
import com.app.edtech.ui.signup.view_model.JoinViewModel
import com.app.edtech.utils.CommonUtils
import com.app.edtech.utils.network_utils.ProcessDialog
import com.app.edtech.utils.network_utils.Status
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class JoinFragment : BaseFragment<FragmentJoinBinding>(){
    private var isPassHidden = true
    private val viewModel: JoinViewModel by viewModels()


    override fun initView(savedInstanceState: Bundle?) {
        setPasswordToggle()
        onClick()
//        binding.passwordToggle.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), PorterDuff.Mode.SRC_IN)
//        clickTermsConditions()
//        clickDontHaveAccount()
    }
  /*  private fun clickDontHaveAccount(){
        val fullText = "Don’t have an account ?  Sign Up"
        val spannableString = SpannableString(fullText)
        val signUpClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.registrationFragment)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(requireActivity(),R.color.theme) // your link color
                ds.isUnderlineText = false
            }
        }

        val signUpStart = fullText.indexOf("Sign Up")
        val signUpEnd = signUpStart + "Sign Up".length

        spannableString.setSpan(signUpClick, signUpStart, signUpEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvDontHaveAccount.text = spannableString
        binding.tvDontHaveAccount.movementMethod = LinkMovementMethod.getInstance()
        binding.tvDontHaveAccount.highlightColor = Color.TRANSPARENT

    }
    private fun clickTermsConditions(){
        val fullText = "I agree to Terms & Conditions and Privacy Policy of the App"
        val spannableString = SpannableString(fullText)
        val termsClickable = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle Terms & Conditions click
                //Toast.makeText(widget.context, "Terms & Conditions clicked", Toast.LENGTH_SHORT).show()
                val bundle = Bundle()
                bundle.putString("screen","termsCondition")
                findNavController().navigate(R.id.termsConditionsFragment,bundle)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(requireActivity(),R.color.theme) // your link color
                ds.isUnderlineText = false
            }
        }

        // Privacy Policy click span
        val privacyClickable = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle Privacy Policy click
                //Toast.makeText(widget.context, "Privacy Policy clicked", Toast.LENGTH_SHORT).show()
                val bundle = Bundle()
                bundle.putString("screen","privacy")
                findNavController().navigate(R.id.termsConditionsFragment,bundle)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(requireActivity(),R.color.theme)
                ds.isUnderlineText = false
            }
        }

        // Apply spans
        val termsStart = fullText.indexOf("Terms & Conditions")
        val termsEnd = termsStart + "Terms & Conditions".length

        val privacyStart = fullText.indexOf("Privacy Policy")
        val privacyEnd = privacyStart + " Privacy Policy".length

        spannableString.setSpan(termsClickable, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(privacyClickable, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvTermsConditions.text = spannableString
        binding.tvTermsConditions.movementMethod = LinkMovementMethod.getInstance()
        binding.tvTermsConditions.highlightColor = Color.TRANSPARENT
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        CommonUtils.touchHideKeyBoard(view,requireActivity())
        setObserver()
    }
    private fun setObserver() {
        viewModel.getSignUpLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "Login success: ${Gson().toJson(it)}")
                    if (it.data?.status=="true"){
                        Toast.makeText(requireContext(), it.data.msg, Toast.LENGTH_SHORT).show()
                        val bundle = Bundle()
                        bundle.putString("phone",viewModel.phone.get())
                        bundle.putString("from","signup")
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
            loginButton.setOnClickListener {
                findNavController().navigate(R.id.signinFragment)
            }
            getOtpButton.setOnClickListener {
                findNavController().navigate(R.id.otpFragment)
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_join
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