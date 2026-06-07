package com.app.gradmo.network_call.repository

import com.app.gradmo.model.add_attendance.AddAttendanceRequest
import com.app.gradmo.model.attendance_students_list.AttendanceStatus
import com.app.gradmo.model.attendance_students_list.GetStudentsForAttendanceRequest
import com.app.gradmo.model.attendance_students_list.StudentUiModel
import com.app.gradmo.model.mark_attendance.AttendanceSubmitRequest
import com.app.gradmo.model.mark_attendance.Student
import com.app.gradmo.network_call.ApiService
import com.app.gradmo.network_call.RetrofitBuilder
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * AttendanceRepository
 *
 * Owns all network calls for the attendance feature.
 * The ViewModel never touches ApiService directly.
 *
 * Inject this via Hilt or pass it through a ViewModelFactory — your call.
 */
class AttendanceRepository() {
    private val service = RetrofitBuilder.apiService

    // ── Fetch students + today's pre-filled status ────────────────

    /**
     * Fetches the batch student list and maps each student's status for TODAY
     * from the cells map. Students with no cell entry for today get null status.
     *
     * @return List of [StudentUiModel] ready for the UI, plus batchStartTime for submit.
     */
    suspend fun fetchStudentsWithTodayStatus(
        token: String,
        batchId: Int,
        today: LocalDate = LocalDate.now()
    ): FetchResult {
        val todayStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE) // "2026-06-07"
        val month = today.monthValue
        val year = today.year

        val request = GetStudentsForAttendanceRequest(
            batch_id = batchId,
            month = month,
            year = year
        )

        val response = service.getStudentsForAttendance(token, request)

        // Defensive: treat missing/empty cells as empty map
        val cells = response.data.cells ?: emptyMap()

        val students = response.data.students.map { apiStudent ->
            val cellKey = "${apiStudent.studentId}_$todayStr"   // e.g. "111_2026-06-07"
            val cell = cells[cellKey]
            StudentUiModel(
                studentId = apiStudent.studentId,
                name = apiStudent.name,
                todayStatus = AttendanceStatus.fromApi(cell?.status),
                existingAttId = cell?.attendanceId
            )
        }

        return FetchResult(
            students = students,
            batchId = response.data.batch_id,
            batchStartTime = response.data.batchStartTime,
            todayDate = todayStr
        )
    }

    // ── Submit / update attendance ────────────────────────────────

    /**
     * Sends the attendance record to the API.
     *
     * [presentStudentIds] = IDs of students marked PRESENT.
     * The API payload sends only present IDs — confirm with backend whether
     * absent students need to be submitted separately or are inferred.
     */
    suspend fun submitAttendance(
        token: String,
        batchId: Int,
        attendanceDate: String,
        batchStartTime: String,
        presentStudentIds: List<Int>
    ) {
        val request = AddAttendanceRequest(
            attendance_date = attendanceDate,
            batch_id = batchId,
            student_ids = presentStudentIds,
            time = batchStartTime
        )
        service.addAttendance(token, request)
    }

    // ── Result wrapper ────────────────────────────────────────────

    data class FetchResult(
        val students: List<StudentUiModel>,
        val batchId: Int,
        val batchStartTime: String,
        val todayDate: String
    )
}