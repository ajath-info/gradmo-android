package com.app.edtech.ui.get_started.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentGetStartedBinding
import com.app.edtech.preferences.Preferences
import com.app.edtech.preferences.USER_TYPE
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GetStartedFragment : BaseFragment<FragmentGetStartedBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        onClick()
    }

    private fun onClick() {
        binding.apply {
            registerAsStudent.setOnClickListener {
                Preferences.setStringPreference(requireContext(), USER_TYPE, "student")
                findNavController().navigate(R.id.joinFragment)
            }
            registerAsTeacher.setOnClickListener {
                Preferences.setStringPreference(requireContext(), USER_TYPE, "teacher")
                findNavController().navigate(R.id.joinFragment)
            }
            registerAsInstitute.setOnClickListener {
                Preferences.setStringPreference(requireContext(), USER_TYPE, "institute")
                findNavController().navigate(R.id.joinFragment)
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_get_started
    }

}