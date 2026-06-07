package com.app.gradmo.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentAboutUsBinding
import com.app.gradmo.databinding.FragmentPrivacyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutUsFragment : BaseFragment<FragmentAboutUsBinding>() {

//    private val viewModel: LibraryViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {}
    private fun clickEvent() {
        binding.backButton.setOnClickListener{
            findNavController().popBackStack()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        clickEvent()
    }
    override fun getLayoutId(): Int = R.layout.fragment_about_us
    private fun setObserver() {
//        viewModel.getLibraryLiveData().observe(viewLifecycleOwner) {
//            when (it.status) {
//                Status.SUCCESS -> {
//                    Log.e("TAG", "Login success: ${Gson().toJson(it)}")
//                    if (it.data?.status == "true") {
//
//                    } else {
//                        Toast.makeText(requireContext(), "${it.data?.message}", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                    ProcessDialog.dismissDialog(true)
//                }
//
//                Status.LOADING -> {
//                    ProcessDialog.showDialog(requireContext(), true)
//                }
//
//                Status.ERROR -> {
//                    Log.e("TAG", "Login Failed: ${it.message}")
//                    ProcessDialog.dismissDialog(true)
//                }
//            }
//        }

    }
    override fun restoreView() {
//        viewModel.getLibraryLiveData().value?.data?.data?.library?.let {
//            setupBooksRecycler(it, true, totalRecords)
//        }
    }

}