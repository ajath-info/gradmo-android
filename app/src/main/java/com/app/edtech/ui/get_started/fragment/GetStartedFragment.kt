package com.app.edtech.ui.get_started.fragment

import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentGetStartedBinding
import com.app.edtech.model.login.request.LoginRequest
import com.app.edtech.preferences.IS_FIRST_LOGIN_DONE
import com.app.edtech.preferences.Preferences
import com.app.edtech.preferences.USER_TYPE
import com.app.edtech.preferences.UserPreference
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GetStartedFragment : BaseFragment<FragmentGetStartedBinding>() {
    private var isFirstLoginDone=""

    override fun initView(savedInstanceState: Bundle?) {
        isFirstLoginDone = Preferences.getStringPreference(requireContext(), IS_FIRST_LOGIN_DONE) ?: ""
        Log.i("TAG", "isFirstLoginDone: "+isFirstLoginDone)
        setUI()
        onClick()
    }
    private fun setUI() {
        if (isFirstLoginDone=="1"){
            binding.apply {
                registerAsStudent.text = "Access as a Student"
                registerAsTeacher.text = "Access as a Teacher"
                registerAsInstitute.text = "Access as an Institute"
            }
        }
    }
    private fun onClick() {
        binding.apply {
            registerAsStudent.setOnClickListener {
                Preferences.setStringPreference(requireContext(), USER_TYPE, "student")
                if (isFirstLoginDone!="1") findNavController().navigate(R.id.joinFragment)
                else findNavController().navigate(R.id.signinFragment)

//                val deviceToken = "test"
//                val userType = Preferences.getStringPreference(requireContext(), USER_TYPE)
//                val deviceId = "test"
//                var request = LoginRequest(name = "Ujjwal", email = "ujjwal@yopmail.com", mobile = "9876543210", password = "Test@123", device_token = deviceToken, device_id = deviceId, device_type = "android", user_type = userType)
//                UserPreference.loginRequest = request
//                val bundle = Bundle()
//                bundle.putString("from","signup")
//                findNavController().navigate(R.id.editProfileNewFragment,bundle)
            }
            registerAsTeacher.setOnClickListener {
                Preferences.setStringPreference(requireContext(), USER_TYPE, "teacher")
                if (isFirstLoginDone!="1") findNavController().navigate(R.id.joinFragment)
                else findNavController().navigate(R.id.signinFragment)
            }
            registerAsInstitute.setOnClickListener {
                Preferences.setStringPreference(requireContext(), USER_TYPE, "institute")
                if (isFirstLoginDone!="1") findNavController().navigate(R.id.joinFragment)
                else findNavController().navigate(R.id.signinFragment)
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_get_started
    }

}