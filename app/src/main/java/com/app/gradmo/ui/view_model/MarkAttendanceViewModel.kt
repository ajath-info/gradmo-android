package com.app.gradmo.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.mark_attendance.AttendanceStatus
import com.app.gradmo.model.mark_attendance.Student
import com.app.gradmo.model.mark_attendance.buildSubmitRequest
import com.app.gradmo.network_call.repository.AttendanceRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AttendanceUiState(
    val students: List<Student>      = emptyList(),
    val isLoading: Boolean           = false,
    val isLoadingMore: Boolean       = false,
    val hasNextPage: Boolean         = false,
    val currentPage: Int             = 0,
    val searchQuery: String          = "",
    val submitState: SubmitState     = SubmitState.Idle,
    val errorMessage: String?        = null
)

sealed class SubmitState {
    object Idle    : SubmitState()
    object Loading : SubmitState()
    object Success : SubmitState()
    data class Error(val message: String) : SubmitState()
}

class MarkAttendanceViewModel(
    private val repository: AttendanceRepository = AttendanceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AttendanceUiState())
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    /**
     * Key: studentId → marked status.
     *
     * This map is intentionally separate from uiState so that pagination
     * (which replaces the visible list) never loses previously marked entries.
     * When a new page is loaded, its students are seeded from their pre-filled
     * API status if not yet overridden by the teacher.
     */
    private val attendanceMap = mutableMapOf<String, AttendanceStatus>()

    companion object {
        private const val PAGE_SIZE = 6
    }

    init {
        loadFirstPage()
    }

    // ── Page loading ──────────────────────────────────────────────

    fun loadFirstPage() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, currentPage = 0) }
            runCatching {
                repository.fetchStudentsPage(page = 1, pageSize = PAGE_SIZE)
            }.onSuccess { (students, hasNext) ->
                seedAttendanceMap(students)
                _uiState.update {
                    it.copy(
                        students    = students,
                        currentPage = 1,
                        hasNextPage = hasNext,
                        isLoading   = false
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoadingMore || !state.hasNextPage || state.searchQuery.isNotEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            val nextPage = state.currentPage + 1
            runCatching {
                repository.fetchStudentsPage(page = nextPage, pageSize = PAGE_SIZE)
            }.onSuccess { (newStudents, hasNext) ->
                seedAttendanceMap(newStudents)
                _uiState.update {
                    it.copy(
                        students      = it.students + newStudents,
                        currentPage   = nextPage,
                        hasNextPage   = hasNext,
                        isLoadingMore = false
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoadingMore = false, errorMessage = e.message) }
            }
        }
    }

    // ── Attendance marking ────────────────────────────────────────

    /**
     * Toggle or set a student's attendance.
     * If the same status is tapped again, it deselects (sets to null / unmarked).
     */
    fun markAttendance(studentId: String, newStatus: AttendanceStatus) {
        val current = attendanceMap[studentId]
        if (current == newStatus) {
            attendanceMap.remove(studentId)
        } else {
            attendanceMap[studentId] = newStatus
        }
        // Notify list to redraw the affected item
        _uiState.update { it.copy() }
    }

    fun getAttendanceStatus(studentId: String): AttendanceStatus? = attendanceMap[studentId]

    // ── Search ────────────────────────────────────────────────────

    fun updateSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    /**
     * Filters the already-loaded student list locally.
     * Pagination is paused while a search query is active.
     */
    fun getFilteredStudents(): List<Student> {
        val query = _uiState.value.searchQuery.trim()
        return if (query.isEmpty()) {
            _uiState.value.students
        } else {
            _uiState.value.students.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.rollNumber.toString().contains(query)
            }
        }
    }

    // ── Submit ────────────────────────────────────────────────────

    fun submitAttendance() {
        viewModelScope.launch {
            _uiState.update { it.copy(submitState = SubmitState.Loading) }
            val request = buildSubmitRequest(attendanceMap.toMap())
            runCatching {
                repository.submitAttendance(request)
            }.onSuccess {
                _uiState.update { it.copy(submitState = SubmitState.Success) }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(submitState = SubmitState.Error(e.message ?: "Submission failed"))
                }
            }
        }
    }

    fun resetSubmitState() {
        _uiState.update { it.copy(submitState = SubmitState.Idle) }
    }

    // ── Helpers ───────────────────────────────────────────────────

    /** Seed attendanceMap from API-provided pre-filled status, without overwriting teacher changes. */
    private fun seedAttendanceMap(students: List<Student>) {
        students.forEach { student ->
            if (!attendanceMap.containsKey(student.id) && student.status != null) {
                attendanceMap[student.id] = student.status
            }
        }
    }
}
/*
class MarkAttendanceViewModel : ViewModel() {

    private val getAttendanceLiveData = SingleLiveEvent<Resources<AttendanceListResponse>>()

    fun getAttendanceLiveDataLiveData(): LiveData<Resources<AttendanceListResponse>> {
        return getAttendanceLiveData
    }

    fun hitAttendanceLiveDataDataApi(token: String, request: AttendanceListRequest) {
        try {
            getAttendanceLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    getAttendanceLiveData.postValue(
                        Resources.success(
                            ApiRepository().getAttendanceApi(token, request)
                        )
                    )
                } catch (ex: Exception) {
                    getAttendanceLiveData.postValue(Resources.error(ex.localizedMessage, null))
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}*/
