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
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.ui.adapter.StudentAttendanceAdapter
import com.app.gradmo.ui.view_model.SubmitState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

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
        setupRecyclerView()
        setupSearch()
        setupSubmitButton()
        observeState()
    }

    // ── Setup ─────────────────────────────────────────────────────

    private fun setupRecyclerView() {
        adapter = StudentAttendanceAdapter(
            onMark = { studentId, status ->
                viewModel.markAttendance(studentId, status)
                adapter.refreshItem(studentId)
            },
            getStatus = viewModel::getAttendanceStatus
        )

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudents.layoutManager = layoutManager
        binding.rvStudents.adapter       = adapter

        // Pagination: trigger loadNextPage when last item is nearly visible
        binding.rvStudents.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return                              // only trigger on downward scroll
                val last    = layoutManager.findLastVisibleItemPosition()
                val total   = layoutManager.itemCount
                val state   = viewModel.uiState.value
                if (!state.isLoadingMore && state.hasNextPage &&
                    state.searchQuery.isEmpty() &&
                    last >= total - 3) {                         // load when 3 items from end
                    viewModel.loadNextPage()
                }
            }
        })

        binding.btnRetry.setOnClickListener { viewModel.loadFirstPage() }
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
                    renderLoadMore(state.isLoadingMore)
                    renderSubmit(state.submitState)

                    // Always re-submit filtered list to adapter
                    adapter.submitList(viewModel.getFilteredStudents().toList())
                }
            }
        }
    }

    private fun renderList(isLoading: Boolean, errorMessage: String?) {
        binding.progressBarFull.visibility = if (isLoading) View.VISIBLE  else View.GONE
        binding.rvStudents.visibility      = if (isLoading) View.GONE     else View.VISIBLE
        binding.groupError.visibility      = if (errorMessage != null && !isLoading) View.VISIBLE else View.GONE
        binding.tvError.text               = errorMessage
    }

    private fun renderLoadMore(isLoadingMore: Boolean) {
        binding.progressBarPaging.visibility = if (isLoadingMore) View.VISIBLE else View.GONE
    }

    private fun renderSubmit(submitState: SubmitState) {
        when (submitState) {
            is SubmitState.Idle    -> {
                binding.btnSubmit.isEnabled = true
                binding.progressSubmit.visibility = View.GONE
            }
            is SubmitState.Loading -> {
                binding.btnSubmit.isEnabled = false
                binding.progressSubmit.visibility = View.VISIBLE
            }
            is SubmitState.Success -> {
                binding.progressSubmit.visibility = View.GONE
                Snackbar.make(binding.root, "Attendance submitted!", Snackbar.LENGTH_SHORT).show()
                viewModel.resetSubmitState()
            }
            is SubmitState.Error   -> {
                binding.btnSubmit.isEnabled = true
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

/*
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
}*/
