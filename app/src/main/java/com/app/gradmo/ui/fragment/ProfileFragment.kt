package com.app.gradmo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.gradmo.R
import com.app.gradmo.databinding.FragmentProfileBinding
import com.app.gradmo.model.enums.UserType
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.model.login.response.UserData
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.preferences.USER_TYPE
import com.app.gradmo.preferences.UserPreference
import com.bumptech.glide.Glide


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        setUserType()
        setUI()
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    private fun setUserType() {
        val userType = Preferences.getStringPreference(requireContext(), USER_TYPE)
        when (userType) {
            "student" -> UserPreference.userType = UserType.STUDENT
            "teacher" -> UserPreference.userType = UserType.TEACHER
            "institute" -> UserPreference.userType = UserType.INSTITUTE
        }
    }

    private fun setUI() {
        val userData = Preferences.getCustomModelPreference<LoginResponse>(
            requireContext(), LOGIN_DATA
        )?.data ?: UserData()

        binding.apply {
            when (UserPreference.userType) {
                UserType.STUDENT -> {
                    educationalInfoText.isVisible = true
                    etSchoolName.isVisible = true
                    etGrade.isVisible = true
                }
                UserType.TEACHER -> {
                    educationalInfoText.isVisible = false
                    etSchoolName.isVisible = false
                    etGrade.isVisible = false
                }
                UserType.INSTITUTE -> { }
            }

            nameLabel.text = userData.name
            etName.text = userData.name
            etEmail.text = userData.email
            etPhoneNumber.text = userData.mobile
            etLocality.text = userData.address
            etState.text = userData.state
            etCity.text = userData.city
            etPincode.text = userData.pincode
            etSchoolName.text = userData.schoolCollegeName
            etGrade.text = userData.grade

            Glide.with(requireContext())
                .load(userData.image)
                .placeholder(R.drawable.profile_placeholder)
                .into(ivUserImage)
        }
    }
}