package com.app.edtech.ui.fragment

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentBatchDetailBinding
import com.app.edtech.databinding.FragmentSeeAttendenceBinding
import com.app.edtech.model.batch_detail.BatchDetails
import com.app.edtech.model.institute_detail.response.InstituteDetailResponse
import com.app.edtech.model.login.response.LoginResponse
import com.app.edtech.model.static.BatchDetailItem
import com.app.edtech.model.static.StaticLists
import com.app.edtech.preferences.LOGIN_DATA
import com.app.edtech.preferences.Preferences
import com.app.edtech.ui.activity.ZoomVideoActivity
import com.app.edtech.ui.adapter.AdapterBatchDetailsItems
import com.app.edtech.ui.view_model.BatchDetailsViewModel
import com.app.edtech.ui.view_model.SeeAttendenceViewModel
import com.app.edtech.utils.CommonUtils.convertTimeFormat
import com.app.edtech.utils.network_utils.ProcessDialog
import com.app.edtech.utils.network_utils.Status
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import kotlin.getValue

class SeeAttendenceFragment : BaseFragment<FragmentSeeAttendenceBinding>() {
    private val viewModel: SeeAttendenceViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
        // Access token available if needed for API call later
        // val accessToken = Preferences.getCustomModelPreference<LoginResponse>(
        //     requireContext(), LOGIN_DATA
        // )?.data?.accessToken
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        clickEvent()
    }

    // ── Observers ─────────────────────────────────────────────────────────────

    private fun setObserver() {
        viewModel.attendanceData.observe(viewLifecycleOwner) { data ->
            updateMonthTitle(data.year, data.month)
            binding.calendarView.setMonthData(data)
            binding.tvAttendanceSummary.text =
                "Attendance for the month : ${data.presentCount}/${data.totalCount} (${data.percentage}%)"
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    // ── Click Events ──────────────────────────────────────────────────────────

    private fun clickEvent() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnPreviousMonth.setOnClickListener {
            viewModel.goToPreviousMonth()
        }

        binding.btnNextMonth.setOnClickListener {
            viewModel.goToNextMonth()
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun updateMonthTitle(year: Int, month: Int) {
        val monthName = DateFormatSymbols().months[month - 1]   // month is 1-based
        binding.tvMonthYear.text = "$monthName $year"
    }

    override fun getLayoutId(): Int = R.layout.fragment_see_attendence

    override fun restoreView() {

    }
}