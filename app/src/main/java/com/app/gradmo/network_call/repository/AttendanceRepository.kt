package com.app.gradmo.network_call.repository

import com.app.gradmo.model.mark_attendance.AttendanceStatus
import com.app.gradmo.model.mark_attendance.AttendanceSubmitRequest
import com.app.gradmo.model.mark_attendance.Student
import kotlinx.coroutines.delay

/**
 * ─────────────────────────────────────────────────────────────────
 *  AttendanceRepository
 *
 *  ALL network calls are isolated here.
 *  To wire up real APIs:
 *    1. Replace fetchStudentsPage() body with your Retrofit/Ktor call.
 *    2. Replace submitAttendance() body with your Retrofit/Ktor call.
 *    3. Delete the dummy data block at the bottom of this file.
 * ─────────────────────────────────────────────────────────────────
 */
class AttendanceRepository {

    // ── Public API ────────────────────────────────────────────────

    /**
     * Fetch one page of students.
     *
     * @param page     1-based page index
     * @param pageSize number of students per page
     * @return Pair<List<Student>, hasNextPage: Boolean>
     *
     * Replace with:
     *   val response = apiService.getStudents(page = page, pageSize = pageSize)
     *   return Pair(response.students.map { it.toDomain() }, response.hasNext)
     */
    suspend fun fetchStudentsPage(page: Int, pageSize: Int): Pair<List<Student>, Boolean> {
        delay(600) // simulate network latency — remove when using real API

        val fromIndex = (page - 1) * pageSize
        val toIndex   = minOf(fromIndex + pageSize, DUMMY_STUDENTS.size)

        if (fromIndex >= DUMMY_STUDENTS.size) return Pair(emptyList(), false)

        val slice      = DUMMY_STUDENTS.subList(fromIndex, toIndex)
        val hasNextPage = toIndex < DUMMY_STUDENTS.size
        return Pair(slice, hasNextPage)
    }

    /**
     * Submit the marked attendance records.
     *
     * Replace with:
     *   val response = apiService.submitAttendance(request)
     *   if (!response.isSuccessful) throw Exception("Submit failed: ${response.code()}")
     */
    suspend fun submitAttendance(request: AttendanceSubmitRequest) {
        delay(800) // simulate network latency — remove when using real API
        // TODO: replace with actual API call
    }

    // ── Dummy data (DELETE when using real API) ───────────────────

    companion object {
        private val DUMMY_STUDENTS = listOf(
            // Some students have pre-filled attendance (simulates API returning prior status)
            Student("s01", "Esther Howard",    null, 1,  AttendanceStatus.PRESENT),
            Student("s02", "Savannah Nguyen",  null, 2,  null),
            Student("s03", "Marvin McKinney",  null, 3,  AttendanceStatus.ABSENT),
            Student("s04", "Jacob Jones",      null, 4,  null),
            Student("s05", "Floyd Miles",      null, 5,  AttendanceStatus.PRESENT),
            Student("s06", "Annette Black",    null, 6,  null),
            Student("s07", "Bessie Cooper",    null, 7,  null),
            Student("s08", "Darrell Steward",  null, 8,  AttendanceStatus.ABSENT),
            Student("s09", "Cody Fisher",      null, 9,  null),
            Student("s10", "Kristin Watson",   null, 10, null),
            Student("s11", "Jerome Bell",      null, 11, AttendanceStatus.PRESENT),
            Student("s12", "Ralph Edwards",    null, 12, null),
            Student("s13", "Courtney Henry",   null, 13, null),
            Student("s14", "Albert Flores",    null, 14, AttendanceStatus.ABSENT),
            Student("s15", "Theresa Webb",     null, 15, null),
            Student("s16", "Arlene McCoy",     null, 16, null),
        )
    }
}