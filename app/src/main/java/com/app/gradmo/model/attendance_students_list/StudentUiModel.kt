package com.app.gradmo.model.attendance_students_list

data class StudentUiModel(
    val studentId: Int,
    val name: String,
    val todayStatus: AttendanceStatus?,
    val existingAttId: Int? = null
)

enum class AttendanceStatus {
    PRESENT, ABSENT;

    companion object {
        /** Maps the API's "present"/"absent" strings safely. */
        fun fromApi(value: String?): AttendanceStatus? = when (value?.lowercase()) {
            "present" -> PRESENT
            "absent"  -> ABSENT
            else      -> null
        }
    }
}