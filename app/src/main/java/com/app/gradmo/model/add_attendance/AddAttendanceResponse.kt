package com.app.gradmo.model.add_attendance

data class AddAttendanceResponse(
    val attendance_date: String,
    val batchId: Int,
    val date: String,
    val msg: String,
    val results: List<Result>,
    val status: String
) {
    data class Result(
        val attendanceId: Int,
        val attendance_date: String,
        val date: String,
        val msg: String,
        val status: String,
        val studentId: Int,
        val time: String
    )
}