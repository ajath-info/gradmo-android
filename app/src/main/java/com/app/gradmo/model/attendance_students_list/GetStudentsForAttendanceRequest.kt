package com.app.gradmo.model.attendance_students_list

data class GetStudentsForAttendanceRequest(
    val batch_id: Int,
    val month: Int,
    val year: Int
)
