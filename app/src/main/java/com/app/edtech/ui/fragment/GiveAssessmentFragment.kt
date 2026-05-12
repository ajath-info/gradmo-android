package com.app.edtech.ui.fragment

// ─────────────────────────────────────────────────────────────
// GiveAssessmentFragment.kt
// ─────────────────────────────────────────────────────────────

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentGiveAssessmentBinding
import com.app.edtech.model.static.AssessmentQuestion
import com.app.edtech.ui.adapter.AssessmentOptionAdapter
import com.app.edtech.ui.view_model.GiveAssessmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GiveAssessmentFragment : BaseFragment<FragmentGiveAssessmentBinding>() {

    private val viewModel: GiveAssessmentViewModel by viewModels()
    private lateinit var optionAdapter: AssessmentOptionAdapter

    // ── Lifecycle ─────────────────────────────────────────────

    override fun initView(savedInstanceState: Bundle?) {
        setupUI()
        clickEvent()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        // Start 30-minute timer (pass a different value or let the API control it)
        viewModel.startTimer(durationSeconds = 30 * 60)
    }

    override fun getLayoutId(): Int = R.layout.fragment_give_assessment

    override fun restoreView() {
        // Restore from ViewModel LiveData — observers handle it automatically
    }

    // ── UI Setup ──────────────────────────────────────────────

    private fun setupUI() {
        binding.rvOptions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOptions.isNestedScrollingEnabled = false
    }

    // ── Observers ─────────────────────────────────────────────

    private fun setObserver() {

        // Questions list loaded
        viewModel.questions.observe(viewLifecycleOwner) { questions ->
            if (questions.isNullOrEmpty()) return@observe
            // Render initial question (currentIndex observer will also fire)
        }

        // Current question index changed
        viewModel.currentIndex.observe(viewLifecycleOwner) { index ->
            val questions = viewModel.questions.value ?: return@observe
            val question  = questions.getOrNull(index) ?: return@observe
            val total     = questions.size

            renderQuestion(question, index, total)
            updateProgressBar(index + 1, total)   // +1 for display (1-based)
            updateNavigationButtons(index, total)
        }

        // Selected options state (restored after back navigation)
        viewModel.selectedOptions.observe(viewLifecycleOwner) {
            val selectedId = viewModel.getSelectedOptionForCurrentQuestion()
            if (::optionAdapter.isInitialized) {
                optionAdapter.setSelectedOption(selectedId)
            }
        }

        // Timer
        viewModel.timerText.observe(viewLifecycleOwner) { time ->
            binding.tvTimer.text = time
        }

        // Timer finished
        viewModel.timerFinished.observe(viewLifecycleOwner) { finished ->
            if (finished == true) {
                onTimerFinished()
            }
        }
    }

    // ── Rendering ─────────────────────────────────────────────

    private fun renderQuestion(
        question: AssessmentQuestion,
        index: Int,
        total: Int
    ) {
        // Question label e.g. "Question 19"
        binding.tvQuestionLabel.text = "Question ${index + 1}"

        // Question text
        binding.tvQuestionText.text = question.questionText

        // Optional image
        if (!question.imageUrl.isNullOrBlank()) {
            binding.ivQuestionImage.visibility = View.VISIBLE
            // Load with Glide / Coil — uncomment whichever you use:
            // Glide.with(this).load(question.imageUrl).into(binding.ivQuestionImage)
            // binding.ivQuestionImage.load(question.imageUrl)
        } else {
            binding.ivQuestionImage.visibility = View.GONE
        }

        // Options adapter
        val preselectedId = viewModel.getSelectedOptionForCurrentQuestion()
        optionAdapter = AssessmentOptionAdapter(
            options = question.options,
            onOptionSelected = { optionId ->
                viewModel.selectOption(question.id, optionId)
                optionAdapter.setSelectedOption(optionId)
            }
        )
        optionAdapter.setSelectedOption(preselectedId)
        binding.rvOptions.adapter = optionAdapter
    }

    private fun updateProgressBar(currentQuestion: Int, total: Int) {
        binding.tvQuestionCounter.text = "$currentQuestion/$total"

        // Animate the filled progress view width proportionally
        val filledView = binding.progressFilled
        filledView.post {
            val parentWidth = (filledView.parent as ViewGroup).width
            val targetWidth = ((currentQuestion.toFloat() / total) * parentWidth).toInt()
            filledView.layoutParams = filledView.layoutParams.apply {
                width = targetWidth
            }
            filledView.requestLayout()
        }
    }

    private fun updateNavigationButtons(index: Int, total: Int) {
        // "Back" button disabled on first question
        binding.btnBack.isEnabled = index > 0
        binding.btnBack.alpha = if (index > 0) 1f else 0.4f

        // "Next" becomes "Submit" on last question
        binding.btnNext.text = if (index == total - 1) "Submit" else "Next"
    }

    // ── Click Events ──────────────────────────────────────────

    private fun clickEvent() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnBack.setOnClickListener {
            viewModel.goToPreviousQuestion()
        }

        binding.btnNext.setOnClickListener {
            if (viewModel.isLastQuestion()) {
                submitAssessment()
            } else {
                viewModel.goToNextQuestion()
            }
        }
    }

    // ── Actions ───────────────────────────────────────────────

    private fun submitAssessment() {
        viewModel.stopTimer()
        // TODO: Call API with collected answers from viewModel.selectedOptions
        // val answers = viewModel.selectedOptions.value
        // viewModel.submitAnswers(answers)

        // Navigate to result screen
        // findNavController().navigate(R.id.action_giveAssessment_to_result)
    }

    private fun onTimerFinished() {
        // Auto-submit when timer runs out
        submitAssessment()
    }
}


