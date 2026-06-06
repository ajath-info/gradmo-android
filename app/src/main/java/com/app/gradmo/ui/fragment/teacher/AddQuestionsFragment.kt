package com.app.gradmo.ui.fragment.teacher

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentAddQuestionsBinding
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.model.questions.ExamArgs
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.adapter.QuestionPagerAdapter
import com.app.gradmo.ui.view_model.AddQuestionsViewModel
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddQuestionsFragment : BaseFragment<FragmentAddQuestionsBinding>() {

    private val viewModel: AddQuestionsViewModel by viewModels()
    private lateinit var adapter: QuestionPagerAdapter

    private var pendingImagePosition = -1

    // ── Image picker ──────────────────────────────────────────────────────
    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri ?: return@registerForActivityResult
        if (pendingImagePosition >= 0) {
            adapter.setImageForQuestion(pendingImagePosition, uri)
            pendingImagePosition = -1
        }
    }

    // ── BaseFragment ──────────────────────────────────────────────────────

    override fun getLayoutId(): Int = R.layout.fragment_add_questions

    override fun initView(savedInstanceState: Bundle?) {
        readArgs()
        setupViewPager()
        setupBottomBar()
        setObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun restoreView() { /* no-op */ }

    // ── Args ──────────────────────────────────────────────────────────────

    private fun readArgs() {
        arguments?.let { args ->
            viewModel.batchId       = args.getString(ExamArgs.BATCH_ID, "0")
            viewModel.examName      = args.getString(ExamArgs.NAME, "") ?: ""
            viewModel.timeDuration  = args.getInt(ExamArgs.DURATION, 0)
            viewModel.scheduledDate = args.getString(ExamArgs.DUE_DATE, "") ?: ""
            viewModel.scheduledTime = args.getString(ExamArgs.DUE_TIME, "") ?: ""

            Log.i("TAG", "readArgs: batchId "+viewModel.batchId)
        }
    }

    // ── ViewPager ─────────────────────────────────────────────────────────

    private fun setupViewPager() {
        adapter = QuestionPagerAdapter(
            questions     = viewModel.getQuestionList(),
            onBrowseClick = { position ->
                pendingImagePosition = position
                imagePicker.launch("image/*")
            },
            onDeleteClick = { position -> confirmDelete(position) }
        )

        binding.questionViewPager.adapter = adapter
        binding.questionViewPager.offscreenPageLimit = 1

        binding.questionViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    updateCounter(position)
                }
            }
        )
        updateCounter(0)
    }

    // ── Bottom bar ────────────────────────────────────────────────────────

    private fun setupBottomBar() {
        binding.addQuestionButton.setOnClickListener {
            val newIndex = viewModel.addQuestion()
            adapter.refreshAll()
            binding.questionViewPager.post {
                binding.questionViewPager.currentItem = newIndex
                updateCounter(newIndex)
            }
        }

        binding.finishTestButton.setOnClickListener { onFinishTest() }
    }

    // ── Delete ────────────────────────────────────────────────────────────

    private fun confirmDelete(position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Remove Question")
            .setMessage("Remove Question ${position + 1}? This cannot be undone.")
            .setPositiveButton("Remove") { _, _ -> deleteQuestion(position) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteQuestion(position: Int) {
        val navigateTo = viewModel.removeQuestion(position)
        if (navigateTo == -1) {
            Toast.makeText(requireContext(), "Must have at least one question.", Toast.LENGTH_SHORT).show()
            return
        }
        adapter.refreshAll()
        binding.questionViewPager.post {
            binding.questionViewPager.setCurrentItem(navigateTo, false)
            updateCounter(navigateTo)
        }
    }

    // ── Observers ─────────────────────────────────────────────────────────

    private fun setObservers() {
        viewModel.submitResult.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Status.LOADING -> ProcessDialog.showDialog(requireContext(), true)

                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog(true)
                    Toast.makeText(requireContext(), result.data?.msg ?: "Exam created!", Toast.LENGTH_SHORT).show()
                    // Pop both AddQuestionsFragment AND CreateExamDetailsFragment
                    // in one call, landing on TeacherExamListFragment.
                    findNavController().popBackStack(R.id.teacherExamListFragment, false)
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog(true)
                    showError(result.message ?: "Something went wrong.")
                }
            }
        }
    }

    // ── Submit ────────────────────────────────────────────────────────────

    private fun onFinishTest() {
        val accessToken = Preferences
            .getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)
            ?.data?.accessToken ?: ""

        viewModel.submitExam(
            token           = "Bearer $accessToken",
            contentResolver = requireContext().contentResolver,
            cacheDir        = requireContext().cacheDir
        )
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private fun updateCounter(currentIndex: Int) {
        binding.questionCounter.text = "${currentIndex + 1} / ${viewModel.getCurrentCount()}"
    }

    private fun showError(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Incomplete")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}