package com.app.gradmo.ui.fragment

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentFilterInstituteBottomSheetBinding
import com.app.gradmo.databinding.FragmentLibraryBinding
import com.app.gradmo.databinding.FragmentPrivacyBinding
import com.app.gradmo.model.library_list.LibraryListRequest
import com.app.gradmo.model.library_list.LibraryListResponse
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.adapter.AdapterLibraryBook
import com.app.gradmo.ui.view_model.ContentDataViewModel
import com.app.gradmo.ui.view_model.LibraryViewModel
import com.app.gradmo.utils.CommonUtils
import com.app.gradmo.utils.CommonUtils.updateModeUI
import com.app.gradmo.utils.CommonUtils.updateSortUI
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class PrivacyFragment : BaseFragment<FragmentPrivacyBinding>() {

    private val viewModel: ContentDataViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.accessToken
        viewModel.hitContentDataApi("Bearer $accessToken")
    }
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
    override fun getLayoutId(): Int = R.layout.fragment_privacy
    private fun setObserver() {
        viewModel.getContentDataLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "Content Api success: ${Gson().toJson(it)}")
                    if (it.data?.status == "true") {
                        binding.contentText.text = it.data.data.privacy_policy.content
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.msg}", Toast.LENGTH_SHORT).show()
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Content Api Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }

    }
    override fun restoreView() {
//        viewModel.getLibraryLiveData().value?.data?.data?.library?.let {
//            setupBooksRecycler(it, true, totalRecords)
//        }
    }

}