package com.app.gradmo.ui.fragment.teacher


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.databinding.FragmentMarkAttendanceBinding
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.adapter.StudentAttendanceAdapter
import com.app.gradmo.ui.view_model.MarkAttendanceViewModel
import com.app.gradmo.ui.view_model.SubmitState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MarkAttendanceFragment : Fragment() {

    private var _binding: FragmentMarkAttendanceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MarkAttendanceViewModel by viewModels()

    private lateinit var adapter: StudentAttendanceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarkAttendanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set on ViewModel BEFORE loadStudents() — init block is intentionally absent
        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.accessToken
        viewModel.token   = "Bearer $accessToken"
        viewModel.batchId = arguments?.getString("batch_id")?.toIntOrNull() ?: 0

        setTodayChip()
        setupRecyclerView()
        setupSearch()
        setupSubmitButton()
        observeState()

        viewModel.loadStudents()   // called here so token + batchId are guaranteed set
    }

    // ── Setup ─────────────────────────────────────────────────────

    private fun setTodayChip() {
        val today = LocalDate.now()
        val fmt   = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())
        binding.tvDate.text = "Today · ${today.format(fmt)}"
    }

    private fun setupRecyclerView() {
        adapter = StudentAttendanceAdapter(
            onMark    = { studentId, status ->
                viewModel.markAttendance(studentId, status)
                // refreshItem triggers a lightweight partial bind (only buttons update)
                adapter.refreshItem(studentId)
            },
            getStatus = viewModel::getAttendanceStatus
        )

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudents.layoutManager = layoutManager
        binding.rvStudents.adapter       = adapter
        binding.rvStudents.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        // Client-side pagination: load next slice when near bottom
        binding.rvStudents.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return
                val last  = layoutManager.findLastVisibleItemPosition()
                val total = layoutManager.itemCount
                if (viewModel.canLoadMore() && last >= total - 3) {
                    viewModel.loadNextPage()
                }
            }
        })

        binding.btnRetry.setOnClickListener { viewModel.loadStudents() }
    }

    private fun setupSearch() {
        binding.etSearch.doAfterTextChanged { editable ->
            viewModel.updateSearch(editable?.toString() ?: "")
        }
    }

    private fun setupSubmitButton() {
        binding.btnSubmit.setOnClickListener {
            viewModel.submitAttendance()
        }
    }

    // ── Observation ───────────────────────────────────────────────

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderList(state.isLoading, state.errorMessage)
                    renderSubmit(state.submitState)
                    // Push filtered/paginated list to adapter on every state change
                    adapter.submitList(viewModel.getFilteredStudents().toList())
                }
            }
        }
    }

    private fun renderList(isLoading: Boolean, errorMessage: String?) {
        binding.progressBarFull.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvStudents.visibility      = if (isLoading) View.GONE    else View.VISIBLE
        binding.groupError.visibility      =
            if (errorMessage != null && !isLoading) View.VISIBLE else View.GONE
        binding.tvError.text               = errorMessage
    }

    private fun renderSubmit(submitState: SubmitState) {
        when (submitState) {
            is SubmitState.Idle -> {
                binding.btnSubmit.isEnabled       = true
                binding.progressSubmit.visibility = View.GONE
            }
            is SubmitState.Loading -> {
                binding.btnSubmit.isEnabled       = false
                binding.progressSubmit.visibility = View.VISIBLE
            }
            is SubmitState.Success -> {
                binding.progressSubmit.visibility = View.GONE
                Snackbar.make(binding.root, "Attendance saved!", Snackbar.LENGTH_SHORT).show()
                viewModel.resetSubmitState()
            }
            is SubmitState.Error -> {
                binding.btnSubmit.isEnabled       = true
                binding.progressSubmit.visibility = View.GONE
                Snackbar.make(binding.root, submitState.message, Snackbar.LENGTH_LONG).show()
                viewModel.resetSubmitState()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}