package com.app.gradmo.model.mark_attendance

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Payload sent to the attendance submission API.
 *
 * @param date     ISO-8601 date string (today by default, passed in by the caller)
 * @param records  One entry per student that was marked
 */
data class AttendanceSubmitRequest(
    val date: String,
    val records: List<AttendanceRecord>
)

data class AttendanceRecord(
    val studentId: String,
    val status: AttendanceStatus
)

/** Convenience: build today's submission payload from the in-memory state map. */
fun buildSubmitRequest(
    attendanceMap: Map<String, AttendanceStatus>,
    date: LocalDate = LocalDate.now()
): AttendanceSubmitRequest {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    return AttendanceSubmitRequest(
        date    = date.format(formatter),
        records = attendanceMap.map { (id, status) ->
            AttendanceRecord(studentId = id, status = status)
        }
    )
}