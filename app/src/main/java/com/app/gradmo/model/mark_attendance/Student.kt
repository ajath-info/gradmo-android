package com.app.gradmo.model.mark_attendance

/**
 * Represents a student in the attendance list.
 *
 * @param id         Unique student identifier (from API)
 * @param name       Full name
 * @param avatarUrl  Profile picture URL (nullable)
 * @param rollNumber Display roll/serial number
 * @param status     Pre-filled attendance status from API (null = not yet marked)
 */
data class Student(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val rollNumber: Int,
    val status: AttendanceStatus? = null   // null = unmarked; may be pre-set by API
)

enum class AttendanceStatus {
    PRESENT, ABSENT
}