package com.app.gradmo.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentSeeAttendenceBinding
import com.app.gradmo.ui.view_model.SeeAttendenceViewModel
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