package com.app.gradmo.model.attendance_students_list

/**
 * Response model for the get-students-for-attendance API.
 *
 * The `cells` field uses Map<String, CellEntry> because the keys are dynamic
 * (format: "{studentId}_{date}", e.g. "111_2026-06-05"). Gson deserializes
 * any JSON object into a Map automatically — no custom adapter needed.
 */
data class GetStudentsForAttendanceResponse(
    val status: String,
    val msg: String,
    val `data`: Data
) {
    data class Data(
        val batch_id: Int,
        val year: Int,
        val month: Int,
        val dateFrom: String,
        val dateTo: String,
        val batchStartTime: String,
        val students: List<Student>,
        val dates: List<DateEntry>,
        /**
         * Key format: "{studentId}_{date}"  e.g. "111_2026-06-05"
         * Value: the attendance record for that student on that date.
         *
         * May be null if the server returns `"cells": {}` or omits the key entirely.
         */
        val cells: Map<String, CellEntry>?
    )

    data class Student(
        val studentId: Int,
        val name: String,
        val email: String,
        val mobile: String
    )

    data class DateEntry(
        val date: String,
        val day: Int,
        val weekday: Int,
        val label: String
    )

    data class CellEntry(
        val status: String,       // "present" | "absent"
        val time: String,
        val attendanceId: Int,
        val dayStatus: String
    )
}