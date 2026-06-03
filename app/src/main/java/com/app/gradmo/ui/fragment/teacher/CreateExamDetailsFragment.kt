package com.app.gradmo.ui.fragment.teacher

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentCreateExamDetailsBinding
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.model.questions.ExamArgs
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class CreateExamDetailsFragment : BaseFragment<FragmentCreateExamDetailsBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_create_exam_details

    override fun initView(savedInstanceState: Bundle?) { /* no-op */ }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickEvent()
    }

    override fun restoreView() { /* no-op */ }

    private fun clickEvent() {
        binding.addQuestions.setOnClickListener {
            if (!validateInputs()) return@setOnClickListener

            // ── Read batch_id from whoever launched this fragment ────────
            // Adjust the argument key / source to match your navigation graph.
            val batchId = arguments?.getInt("batch_id", 0) ?: 0

            val name     = binding.heading.text.toString().trim()
            val duration = binding.duration.text.toString().trim().toIntOrNull() ?: 0
            val dueDate  = binding.dueDate.text.toString().trim()   // expect "yyyy-MM-dd"
            val dueTime  = binding.dueTime.text.toString().trim()   // expect "HH:mm"

            val args = Bundle().apply {
                putInt(ExamArgs.BATCH_ID, batchId)
                putString(ExamArgs.NAME,  name)
                putInt(ExamArgs.DURATION, duration)
                putString(ExamArgs.DUE_DATE, dueDate)
                putString(ExamArgs.DUE_TIME, dueTime)
            }

            findNavController().navigate(R.id.addQuestionsFragment, args)
        }
    }

    // ── Validation ────────────────────────────────────────────────────────

    private fun validateInputs(): Boolean {
        val name     = binding.heading.text.toString().trim()
        val duration = binding.duration.text.toString().trim()
        val dueDate  = binding.dueDate.text.toString().trim()
        val dueTime  = binding.dueTime.text.toString().trim()

        return when {
            name.isEmpty() -> {
                binding.heading.error = "Name is required"
                false
            }
            duration.isEmpty() || duration.toIntOrNull() == null -> {
                binding.duration.error = "Enter a valid duration in minutes"
                false
            }
            dueDate.isEmpty() || !isValidDate(dueDate) -> {
                binding.dueDate.error = "Enter date as yyyy-MM-dd"
                false
            }
            dueTime.isEmpty() || !isValidTime(dueTime) -> {
                binding.dueTime.error = "Enter time as HH:mm"
                false
            }
            else -> true
        }
    }

    private fun isValidDate(value: String): Boolean = try {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).also { it.isLenient = false }
            .parse(value) != null
    } catch (e: Exception) { false }

    private fun isValidTime(value: String): Boolean = try {
        SimpleDateFormat("HH:mm", Locale.getDefault()).also { it.isLenient = false }
            .parse(value) != null
    } catch (e: Exception) { false }
}