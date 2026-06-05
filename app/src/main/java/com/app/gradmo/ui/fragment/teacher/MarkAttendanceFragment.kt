package com.app.gradmo.ui.fragment.teacher

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentMarkAttendanceBinding
import com.app.gradmo.databinding.FragmentSeeAttendenceBinding
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.model.staticdata.CalenderModels.CalenderModels
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.view_model.MarkAttendanceViewModel
import com.app.gradmo.ui.view_model.SeeAttendenceViewModel
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormatSymbols
import kotlin.getValue

@AndroidEntryPoint
class MarkAttendanceFragment : BaseFragment<FragmentMarkAttendanceBinding>() {
    private val viewModel: MarkAttendanceViewModel by viewModels()
    private var batch_id = ""

    override fun initView(savedInstanceState: Bundle?) {
        batch_id = arguments?.getString("batch_id", "") ?: ""
        Log.i("TAG", "batch_id: "+batch_id)
        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(
            requireContext(), LOGIN_DATA
        )?.data?.accessToken ?: ""

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        clickEvent()
    }

    // ── Observers ─────────────────────────────────────────────────────────────

    private fun setObserver() {
        viewModel.getAttendanceLiveDataLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog(true)
                    Log.e("TAG", "getAttendanceLiveData success: ${Gson().toJson(it)}")

                    if (it.data?.status == "true") {
                        // Convert raw API response → UI model
                        val uiData = CalenderModels.MonthAttendanceData.fromApiResponse(it.data)

                        } else {
                        Toast.makeText(requireContext(), "${it.data?.msg}", Toast.LENGTH_SHORT).show()
                    }
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog(true)
                    Log.e("TAG", "getAttendanceLiveData Failed: ${it.message}")
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ── Click Events ──────────────────────────────────────────────────────────

    private fun clickEvent() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_mark_attendance

    override fun restoreView() {}
}