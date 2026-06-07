package com.app.gradmo.model.add_attendance

data class AddAttendanceRequest(
    val attendance_date: String,
    val batch_id: Int,
    val student_ids: List<Int>,
    val time: String
)
