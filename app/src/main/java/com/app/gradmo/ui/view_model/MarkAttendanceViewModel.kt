package com.app.gradmo.ui.view_model


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gradmo.model.attendance_students_list.AttendanceStatus
import com.app.gradmo.model.attendance_students_list.StudentUiModel
import com.app.gradmo.network_call.repository.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── UI State ──────────────────────────────────────────────────────

data class AttendanceUiState(
    val students: List<StudentUiModel> = emptyList(),
    val isLoading: Boolean             = false,
    val errorMessage: String?          = null,
    val searchQuery: String            = "",
    val submitState: SubmitState       = SubmitState.Idle
)

sealed class SubmitState {
    object Idle    : SubmitState()
    object Loading : SubmitState()
    object Success : SubmitState()
    data class Error(val message: String) : SubmitState()
}

// ── ViewModel ─────────────────────────────────────────────────────

@HiltViewModel
class MarkAttendanceViewModel @Inject constructor() : ViewModel() {

    // Set by Fragment immediately after viewModels() delegation, before calling loadStudents()
    var token: String = ""
    var batchId: Int = 0

    private val repository = AttendanceRepository()

    private val _uiState = MutableStateFlow(AttendanceUiState())
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    // ── Attendance map ────────────────────────────────────────────
    //
    // Key: studentId (Int)  →  Value: status chosen by teacher (or pre-seeded from API)
    // Intentionally NOT inside uiState — survives search filter changes
    // and won't be wiped by state emissions.
    //
    private val attendanceMap = mutableMapOf<Int, AttendanceStatus>()

    // Cached from fetch — needed for submit payload
    private var batchStartTime: String = ""
    private var todayDate: String      = ""

    // ── Pagination ────────────────────────────────────────────────
    // The full student list is fetched in one call (no server-side paging).
    // Client-side pagination is applied for smooth rendering of large lists.

    private val allStudents = mutableListOf<StudentUiModel>()
    private var currentPage = 0
    private var hasNextPage = false

    companion object {
        private const val PAGE_SIZE = 8
    }

    // No init block — loadStudents() is called from the Fragment after
    // token and batchId are set, so the API fires with correct values.

    // ── Load ──────────────────────────────────────────────────────

    fun loadStudents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                repository.fetchStudentsWithTodayStatus(token = token, batchId = batchId)
            }.onSuccess { result ->
                batchStartTime = result.batchStartTime
                todayDate      = result.todayDate

                allStudents.clear()
                allStudents.addAll(result.students)

                // Seed attendance map from today's pre-filled cells
                // Without overwriting any status the teacher already changed
                result.students.forEach { student ->
                    if (!attendanceMap.containsKey(student.studentId) &&
                        student.todayStatus != null) {
                        attendanceMap[student.studentId] = student.todayStatus
                    }
                }

                // Show first page
                currentPage = 0
                hasNextPage = false
                val firstPage = nextPageSlice()
                _uiState.update {
                    it.copy(
                        students    = firstPage,
                        isLoading   = false,
                        errorMessage = null
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load students")
                }
            }
        }
    }

    // ── Client-side pagination ────────────────────────────────────

    /**
     * Called by the Fragment's scroll listener when near the bottom.
     * Only active when no search query is running.
     */
    fun loadNextPage() {
        if (!hasNextPage || _uiState.value.searchQuery.isNotEmpty()) return
        val nextSlice = nextPageSlice()
        if (nextSlice.isEmpty()) return
        _uiState.update { it.copy(students = it.students + nextSlice) }
    }

    fun canLoadMore(): Boolean = hasNextPage && _uiState.value.searchQuery.isEmpty()

    private fun nextPageSlice(): List<StudentUiModel> {
        val from = currentPage * PAGE_SIZE
        val to   = minOf(from + PAGE_SIZE, allStudents.size)
        if (from >= allStudents.size) {
            hasNextPage = false
            return emptyList()
        }
        currentPage++
        hasNextPage = to < allStudents.size
        return allStudents.subList(from, to)
    }

    // ── Attendance marking ────────────────────────────────────────

    /**
     * Toggles or switches a student's attendance.
     *
     * Rules:
     *  - Tap PRESENT when already PRESENT → deselect (remove from map)
     *  - Tap ABSENT  when already ABSENT  → deselect (remove from map)
     *  - Tap the other button             → switch status
     */
    fun markAttendance(studentId: Int, newStatus: AttendanceStatus) {
        if (attendanceMap[studentId] == newStatus) {
            attendanceMap.remove(studentId)
        } else {
            attendanceMap[studentId] = newStatus
        }
        // No uiState update needed — adapter calls getAttendanceStatus() directly
    }

    fun getAttendanceStatus(studentId: Int): AttendanceStatus? = attendanceMap[studentId]

    // ── Search ────────────────────────────────────────────────────

    fun updateSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    /**
     * Returns filtered list if a query is active, otherwise returns whatever
     * pages have been loaded so far (the visible paginated list).
     */
    fun getFilteredStudents(): List<StudentUiModel> {
        val query = _uiState.value.searchQuery.trim()
        return if (query.isEmpty()) {
            _uiState.value.students
        } else {
            // Search across ALL students, not just loaded pages
            allStudents.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.studentId.toString().contains(query)
            }
        }
    }

    // ── Submit ────────────────────────────────────────────────────

    fun submitAttendance() {
        viewModelScope.launch {
            _uiState.update { it.copy(submitState = SubmitState.Loading) }

            // Only send IDs marked PRESENT
            // Confirm with backend: do absent students need a separate call?
            val presentIds = attendanceMap
                .filter { (_, status) -> status == AttendanceStatus.PRESENT }
                .keys
                .toList()

            runCatching {
                repository.submitAttendance(
                    token             = token,
                    batchId           = batchId,
                    attendanceDate    = todayDate,
                    batchStartTime    = batchStartTime,
                    presentStudentIds = presentIds
                )
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
}