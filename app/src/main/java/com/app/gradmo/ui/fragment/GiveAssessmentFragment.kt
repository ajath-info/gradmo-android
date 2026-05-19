package com.app.gradmo.ui.fragment

// ─────────────────────────────────────────────────────────────
// GiveAssessmentFragment.kt
// ─────────────────────────────────────────────────────────────

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentGiveAssessmentBinding
import com.app.gradmo.model.exam_details.ExamDetailsRequest
import com.app.gradmo.model.exam_details.ExamDetailsResponse
import com.app.gradmo.model.exam_list.UpcomingExamListResponse
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.model.static.AssessmentQuestion
import com.app.gradmo.model.submit_exam.SubmitExamRequest
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.adapter.AssessmentOptionAdapter
import com.app.gradmo.ui.view_model.GiveAssessmentViewModel
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GiveAssessmentFragment : BaseFragment<FragmentGiveAssessmentBinding>() {

    private val viewModel: GiveAssessmentViewModel by viewModels()
    private lateinit var optionAdapter: AssessmentOptionAdapter

    // Holds the full exam data from API
    private var examData: ExamDetailsResponse.Exam? = null

    // Tracks selected option per question: questionId -> selectedOptionIndex (0-3)
    private val selectedAnswers = mutableMapOf<String, Int>()

    // Current question index
    private var currentIndex = 0

    var exam = UpcomingExamListResponse.Data.UpcomingExam()

    // ── Lifecycle ─────────────────────────────────────────────

    override fun initView(savedInstanceState: Bundle?) {
        exam = arguments?.getParcelable<UpcomingExamListResponse.Data.UpcomingExam>("exam") ?: UpcomingExamListResponse.Data.UpcomingExam()
        setupRecycler()
        clickEvent()
        Log.i("TAG", "startedAt startedAt: "+viewModel.startedAt)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.accessToken
        viewModel.hitUpcomingExamsDetailsDataApi(token = "Bearer $accessToken", request = ExamDetailsRequest(exam_id = exam.id.toString()))
    }

    override fun getLayoutId(): Int = R.layout.fragment_give_assessment

    override fun restoreView() {}

    // ── Setup ─────────────────────────────────────────────────

    private fun setupRecycler() {
        binding.rvOptions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOptions.isNestedScrollingEnabled = false
    }

    // ── Observers ─────────────────────────────────────────────

    private fun setObserver() {

        // ── Exam Details API observer
        viewModel.getUpcomingExamsDetailsLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "getUpcomingExamsDetailsLiveData success: ${Gson().toJson(it)}")
                    if (it.data?.status == "true") {
                        it.data.exam.let { exam ->
                            examData = exam
                            currentIndex = 0
                            // timeDuration from API is in minutes e.g. "5"
                            startTimer(exam.timeDuration.toIntOrNull() ?: 30)
                            renderQuestion()
                        }
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.message}", Toast.LENGTH_SHORT).show()
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "getUpcomingExamsDetailsLiveData Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }

        viewModel.getSubmitExamsLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "getSubmitExamsLiveData success: ${Gson().toJson(it)}")
                    if (it.data?.status == "true") {
                        Toast.makeText(requireContext(), "${it.data.message ?: it.data.msg}", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.testCompletedFragment)
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.message ?: it.data?.msg}", Toast.LENGTH_SHORT).show()
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "getSubmitExamsLiveData Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }

        // ── Timer observer
        viewModel.timerText.observe(viewLifecycleOwner) { time ->
            binding.tvTimer.text = time
        }

        // ── Timer finished — auto submit
        viewModel.timerFinished.observe(viewLifecycleOwner) { finished ->
            if (finished == true) submitAssessment()
        }
    }

    // ── Render current question ───────────────────────────────

    private fun renderQuestion() {
        val exam = examData ?: return
        val questions = exam.questionDetails
        val total = questions.size
        val question = questions.getOrNull(currentIndex) ?: return

        // Question label e.g. "Question 3"
        binding.tvQuestionLabel.text = "Question ${currentIndex + 1}"

        // Question text
        binding.tvQuestionText.text = question.question

        // Question image — show only when URL is not empty
        if (question.questionImageUrl.isNotBlank()) {
            binding.ivQuestionImage.visibility = View.VISIBLE
            Glide.with(this)
                .load(question.questionImageUrl)
                .placeholder(R.drawable.exam_placeholder)  // add a placeholder drawable
                .error(R.drawable.exam_placeholder)
                .into(binding.ivQuestionImage)
        } else {
            binding.ivQuestionImage.visibility = View.GONE
        }

        // Parse options from JSON string: "[\"A1\",\"A2\",\"A\",\"A4\"]"
        val optionsList: List<String> = try {
            Gson().fromJson(question.options, Array<String>::class.java).toList()
        } catch (e: Exception) {
            Log.e("TAG", "Options parse error: ${e.message}")
            emptyList()
        }

        // Restore previously selected index for this question (if user navigated back)
        val preSelected = selectedAnswers[question.id] ?: -1

        // Setup options adapter
        optionAdapter = AssessmentOptionAdapter(
            options = optionsList,
            preSelectedIndex = preSelected,
            onOptionSelected = { selectedIndex ->
                // Save the selected index keyed by question id
                selectedAnswers[question.id] = selectedIndex
            }
        )
        binding.rvOptions.adapter = optionAdapter

        // Update progress bar + counter
        updateProgressBar(currentIndex + 1, total)

        // Update Back / Next button states
        updateNavigationButtons(currentIndex, total)
    }

    // ── Progress bar ──────────────────────────────────────────

    private fun updateProgressBar(current: Int, total: Int) {
        binding.tvQuestionCounter.text = "$current/$total"

        // Animate filled portion proportionally
        binding.progressFilled.post {
            val parentWidth = (binding.progressFilled.parent as ViewGroup).width
            val targetWidth = ((current.toFloat() / total) * parentWidth).toInt()
            binding.progressFilled.updateLayoutParams { width = targetWidth }
        }
    }

    // ── Navigation button states ──────────────────────────────

    private fun updateNavigationButtons(index: Int, total: Int) {
        binding.btnBack.isEnabled = index > 0
        binding.btnBack.alpha = if (index > 0) 1f else 0.4f
        binding.btnNext.text = if (index == total - 1) "Submit" else "Next"
    }

    // ── Timer ─────────────────────────────────────────────────

    /**
     * durationMinutes: from API exam.timeDuration e.g. "5" -> 5 minutes
     */
    private fun startTimer(durationMinutes: Int) {
        viewModel.startTimer(durationSeconds = durationMinutes * 60)
    }

    // ── Click events ──────────────────────────────────────────

    private fun clickEvent() {

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnBack.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                renderQuestion()
            }
        }

        binding.btnNext.setOnClickListener {
            val questions = examData?.questionDetails ?: return@setOnClickListener
            if (currentIndex == questions.size - 1) {
                submitAssessment()
            } else {
                currentIndex++
                renderQuestion()
            }
        }
    }

    // ── Submit ────────────────────────────────────────────────

    private fun submitAssessment() {
        viewModel.stopTimer()

        // selectedAnswers: Map<questionId, selectedOptionIndex (0-based)>
        // Convert index -> letter (A/B/C/D) to match API answer format
        val answersPayload = selectedAnswers.map { (questionId, optionIndex) ->
            questionId to indexToOptionLetter(optionIndex)
        }.toMap()

        Log.d("Assessment", "Submitting answers: $answersPayload")
        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.accessToken
        val request = SubmitExamRequest(exam_id = exam.id, started_at = viewModel.startedAt , answers = answersPayload)
        Log.d("AssessmentJson", Gson().toJson(request))

        viewModel.hitSubmitExamsDataApi(token = "Bearer $accessToken", request)
    }

    // Converts 0->A, 1->B, 2->C, 3->D (matches API answer field format)
    private fun indexToOptionLetter(index: Int): String = when (index) {
        0 -> "A"
        1 -> "B"
        2 -> "C"
        3 -> "D"
        else -> ""
    }
}