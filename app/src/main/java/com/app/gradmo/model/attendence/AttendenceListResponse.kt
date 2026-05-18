package com.app.gradmo.model.attendence

data class AttendenceListResponse(
    val attendance: List<Any>,
    val calendar: Calendar,
    val msg: String,
    val pagination: Pagination,
    val status: String,
    val summary: Summary,
    val userType: String
) {
    data class Calendar(
        val `2026-05-01`: String,
        val `2026-05-02`: String,
        val `2026-05-03`: String,
        val `2026-05-04`: String,
        val `2026-05-05`: String,
        val `2026-05-06`: String,
        val `2026-05-07`: String,
        val `2026-05-08`: String,
        val `2026-05-09`: String,
        val `2026-05-10`: String,
        val `2026-05-11`: String,
        val `2026-05-12`: String,
        val `2026-05-13`: String,
        val `2026-05-14`: String,
        val `2026-05-15`: String,
        val `2026-05-16`: String,
        val `2026-05-17`: String,
        val `2026-05-18`: String,
        val `2026-05-19`: String,
        val `2026-05-20`: String,
        val `2026-05-21`: String,
        val `2026-05-22`: String,
        val `2026-05-23`: String,
        val `2026-05-24`: String,
        val `2026-05-25`: String,
        val `2026-05-26`: String,
        val `2026-05-27`: String,
        val `2026-05-28`: String,
        val `2026-05-29`: String,
        val `2026-05-30`: String,
        val `2026-05-31`: String
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