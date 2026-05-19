package com.app.gradmo.model.attendence

data class AttendanceListResponse(
    val attendance: List<Attendance>,
    val calendar: Map<String, String>,   // "2026-05-01" -> "present" | "absent" | "late" | "weekend" | "none" | "future"
    val msg: String,
    val pagination: Pagination,
    val status: String,
    val summary: Summary,
    val userType: String
) {
    data class Attendance(
        val addedId: Int,
        val attendance_date: String,
        val batchId: Int,
        val date: String,
        val dayStatus: String,
        val id: Int,
        val is_late: Int,
        val status: String,
        val studentId: Int,
        val time: String
    )

    data class Pagination(
        val limit: Int,
        val page: Int,
        val total: Int,
        val totalPages: Int,
        val totalRecords: Int
    )

    data class Summary(
        val attendancePercent: Int,
        val countAbsent: Int,
        val countHalf: Int,
        val countLate: Int,
        val countPresent: Int,
        val daysInMonth: Int,
        val daysPresent: Int,
        val month: Int,
        val year: Int
    )
}