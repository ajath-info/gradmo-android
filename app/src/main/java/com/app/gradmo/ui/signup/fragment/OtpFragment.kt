package com.app.gradmo.ui.signup.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.gradmo.R
import com.app.gradmo.databinding.FragmentOtpBinding
import com.app.gradmo.model.login.request.LoginRequest
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.preferences.FCM_TOKEN
import com.app.gradmo.preferences.IS_FIRST_LOGIN_DONE
import com.app.gradmo.preferences.IS_LOGIN
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.preferences.USER_TYPE
import com.app.gradmo.preferences.UserPreference
import com.app.gradmo.ui.activity.HomeActivity
import com.app.gradmo.ui.signup.view_model.SendMailOtpViewModel
import com.app.gradmo.utils.CommonUtils
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.google.gson.Gson

class OtpFragment : Fragment() {
    private lateinit var binding:FragmentOtpBinding
    var from = ""
    var phone = ""
    private var countdown: CountDownTimer?=null
    private val sendEmailOtpViewModel: SendMailOtpViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
           from = it.getString("from").toString()
           phone = it.getString("phone").toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        CommonUtils.touchHideKeyBoard(view,requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpBinding.inflate(layoutInflater)
        initViews()
        setUI()
        return binding.root
    }

    private fun setUI() {
        binding.apply {
            if (from=="login") {
                btnConfirm.text = "Verify your number"
            }else if (from == "forgot"){
                btnConfirm.text = "Verify your number"
            }else{
                btnConfirm.text = "Verify And Create Account"
            }
        }

    }


    private fun initViews() {
        customOtp()
        binding.apply {
            btnConfirm.setOnClickListener {
                checkValidation()
            }
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            clMain.setOnClickListener {
                CommonUtils.hideKeyboard(requireActivity())
            }
        }
    }

    /**
     * function is used to start timer when otp is sent successfully
     */
    fun startTimer() {
        if (countdown != null) {
//            super.onDestroy()
            countdown?.cancel()
        }
        countdown = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                var t1 = ""
                var t2: String
                val min = seconds / 60
                val sec = seconds % 60
                t2 = if (sec < 10) {
                    "0${sec}"
                } else {
                    "$sec"
                }
                if (min < 10) {
                    t1 = "0${min}"
                } else {
                    t2 = "$min"
                }
                binding.tvDidnt.text = "Resend Code in "
                binding.tvTime.visibility = View.VISIBLE
                binding.tvTime.text = "$t1:$t2 minute"
                binding.tvResend.visibility = View.GONE
            }
            @SuppressLint("StringFormatInvalid")
            override fun onFinish() {
                binding.tvTime.visibility = View.GONE
                binding.tvResend.visibility = View.VISIBLE
                binding.tvDidnt.text = "Haven’t received the OTP yet?"
                binding.tvResend.setOnClickListener {
                    val userType = Preferences.getStringPreference(requireActivity(), USER_TYPE)
                    val request = LoginRequest(mobile = phone, user_type = userType)
                    sendEmailOtpViewModel.hitResendLoginOtp(request)
                }
            }
        }.start()
    }

    private fun initObserver() {
        sendEmailOtpViewModel.getVerifyOtpLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.status=="true"){
                        if (from=="login") {
                            Preferences.setStringPreference(requireContext(), IS_FIRST_LOGIN_DONE, "1")
                            if (it.data.data?.isProfileCompleted==1){
                                Preferences.setStringPreference(requireContext(), IS_LOGIN, "2")
                                Preferences.setCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA, it.data)
                                CommonUtils.hideKeyboard(requireActivity())
                                Log.i("TAG", "setObserver: "+Preferences.getStringPreference(requireContext(), FCM_TOKEN))
                                startActivity(Intent(requireActivity(), HomeActivity::class.java))
                                requireActivity().finish()
                            }else{
//                                Preferences.setStringPreference(requireContext(), IS_LOGIN, "2")
                                Preferences.setCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA, it.data)
                                UserPreference.loginRequest = LoginRequest(name = it.data.data?.name, mobile = it.data.data?.mobile, email = it.data.data?.email)
                                UserPreference.studentId = it.data.data?.studentId.toString()
                                val bundle = Bundle()
                                bundle.putString("from","signup")
                                findNavController().navigate(R.id.editProfileNewFragment,bundle)
                            }
                        }else if (from == "forgot"){
                            val bundle = Bundle()
                            bundle.putString("phone",phone)
                            bundle.putString("from","forgot")
                            findNavController().navigate(R.id.newPasswordFragment,bundle)
                        }else if (from == "signup"){
                            UserPreference.studentId = it.data.data?.studentId.toString()
                            UserPreference.loginRequest = LoginRequest(name = it.data.data?.name, mobile = it.data.data?.mobile, email = it.data.data?.email)
                            Preferences.setCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA, it.data)
                            val bundle = Bundle()
                            bundle.putString("from","signup")
                            findNavController().navigate(R.id.editProfileNewFragment,bundle)
                        }

                    }else{
                        Toast.makeText(requireContext(), "${it.data?.msg}", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("TAG", "interest list success: ${Gson().toJson(it)}")

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
        sendEmailOtpViewModel.getResendLoginOtpLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "resend otp: ${Gson().toJson(it)}")
                    if (it.data?.status=="true"){
                        Toast.makeText(requireContext(), it.data.msg, Toast.LENGTH_SHORT).show()
                        startTimer()
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


    private fun checkValidation() {
        val code = "${binding.etOtp1.text}${binding.etOtp2.text}${binding.etOtp3.text}" +
                "${binding.etOtp4.text}"
        if(code.isEmpty()){
            Toast.makeText(requireActivity(), "Please enter otp", Toast.LENGTH_SHORT).show()
        }
        else{
//            if (from=="login" || from=="forgot"|| from=="signup"){
                val userType = Preferences.getStringPreference(requireActivity(), USER_TYPE)
                sendEmailOtpViewModel.hitVerifyOtp(phone, code, userType ?: "")
//            }
        }
    }


    // This function is used for custom otp filed like rounded border and next focus
    private fun customOtp() {
        val etOtp1 = binding.etOtp1
        val etOtp2 = binding.etOtp2
        val etOtp3 = binding.etOtp3
        val etOtp4 = binding.etOtp4
        startTimer()
        etOtp1.addTextChangedListener(GenericTextWatcher(etOtp1,etOtp2))
        etOtp2.addTextChangedListener(GenericTextWatcher(etOtp2,etOtp3))
        etOtp3.addTextChangedListener(GenericTextWatcher(etOtp3,etOtp4))
        etOtp4.addTextChangedListener(GenericTextWatcher(etOtp4,null))

        etOtp1.setOnKeyListener(GenericKeyEvent(etOtp1,null))
        etOtp2.setOnKeyListener(GenericKeyEvent(etOtp2,etOtp1))
        etOtp3.setOnKeyListener(GenericKeyEvent(etOtp3,etOtp2))
        etOtp4.setOnKeyListener(GenericKeyEvent(etOtp4,etOtp3))

    }


    // Class to change focus to another otp filed
    class GenericTextWatcher internal constructor(
        private val currentView: View,
        private val nextView: View?,
    ) :
        TextWatcher {
        override fun afterTextChanged(editable: Editable) { // TODO Auto-generated method stub
            val text = editable.toString()
            when (currentView.id) {
                R.id.etOtp1 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.etOtp2 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.etOtp3 -> if (text.length == 1) nextView!!.requestFocus()
            }
        }

        override fun beforeTextChanged(
            arg0: CharSequence,
            arg1: Int,
            arg2: Int,
            arg3: Int
        ) {

        }

        override fun onTextChanged(
            arg0: CharSequence,
            arg1: Int,
            arg2: Int,
            arg3: Int
        ) {
        }
    }

    class GenericKeyEvent internal constructor(private val currentView: EditText, private val previousView: EditText?) : View.OnKeyListener{
        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if(event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.etOtp1&& currentView.text.isEmpty()) {
                previousView!!.text = null
                previousView.requestFocus()
                return true
            }
            return false
        }
    }

}