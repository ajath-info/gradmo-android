package com.app.gradmo.model.static

import androidx.annotation.ColorInt
import com.app.gradmo.model.attendence.AttendanceListResponse
import com.app.gradmo.model.static.CalenderModels.CalenderModels.AttendanceStatus
import com.app.gradmo.model.static.CalenderModels.CalenderModels.MonthAttendanceData

class CalenderModels {

    class CalenderModels {

        // ─────────────────────────────────────────────────────────────────────────
        // ATTENDANCE STATUS  ←  single source of truth for all statuses & colours
        //
        // To add a new status:  add an enum entry with apiKey + color + showDot
        // To change a colour:   update the color value on the entry
        // To hide a dot:        set showDot = false
        // ─────────────────────────────────────────────────────────────────────────
        enum class AttendanceStatus(
            val apiKey: String,
            @ColorInt val color: Int,
            val showDot: Boolean = true
        ) {
            PRESENT ("present", 0xFF4CAF50.toInt()),   // green
            ABSENT  ("absent",  0xFFE53935.toInt()),   // red
            LATE    ("late",    0xFFFF9800.toInt()),   // orange
            HALF    ("half",    0xFF29B6F6.toInt()),   // light blue
            HOLIDAY ("holiday", 0xFFFFEB3B.toInt()),   // yellow
            WEEKEND ("weekend", 0xFFFFEB3B.toInt()),   // yellow
            NONE    ("none",    0x00000000, false),    // no circle
            FUTURE  ("future",  0x00000000, false),    // no circle
            UNKNOWN ("",        0xFF9E9E9E.toInt());   // grey fallback

            companion object {
                fun fromApiKey(key: String): AttendanceStatus =
                    entries.firstOrNull { it.apiKey == key.lowercase() } ?: UNKNOWN
            }
        }

        // ─────────────────────────────────────────────────────────────────────────
        // MONTH ATTENDANCE DATA  ←  the UI model passed to the calendar view
        // ─────────────────────────────────────────────────────────────────────────
        data class MonthAttendanceData(
            val year: Int,
            val month: Int,                            // 1-based
            val recordMap: Map<Int, AttendanceStatus>, // day-of-month → status
            val presentCount: Int,
            val totalDays: Int,                        // use this in the fragment summary
            val percentage: Int
        ) {
            companion object {
                /**
                 * Convert the raw API response → UI model.
                 * Reads the [calendar] map (keys "yyyy-MM-dd", values status strings).
                 *
                 * HOW TO WIRE THE REAL API:
                 *   val uiData = MonthAttendanceData.fromApiResponse(yourApiResponse)
                 *   _attendanceData.value = uiData
                 */
                fun fromApiResponse(response: AttendanceListResponse): MonthAttendanceData {
                    val recordMap = response.calendar
                        .mapNotNull { (dateStr, statusStr) ->
                            val day = dateStr.split("-").getOrNull(2)?.toIntOrNull()
                            if (day != null) day to AttendanceStatus.fromApiKey(statusStr)
                            else null
                        }
                        .toMap()

                    return MonthAttendanceData(
                        year         = response.summary.year,
                        month        = response.summary.month,
                        recordMap    = recordMap,
                        presentCount = response.summary.countPresent + response.summary.countLate,
                        totalDays    = response.summary.daysInMonth,
                        percentage   = response.summary.attendancePercent
                    )
                }
            }
        }
    }


    /**
     * A single day's attendance record.
     * @param day  Day of month (1–31)
     * @param status  Attendance status for that day
     */
    data class AttendanceDayRecord(
        val day: Int,
        val status: AttendanceStatus
    )




// ─── Static Data Source (replace with API call later) ───────────────────────

    object AttendanceRepository {

        /**
         * Returns static attendance data for a given year/month.
         *
         * HOW TO REPLACE WITH API:
         *  1. Remove this function (or keep it as a fallback/mock).
         *  2. In your ViewModel, call your Retrofit/API service.
         *  3. Map the API response into a [MonthAttendanceData] object using [mapApiResponse].
         *  4. Post it to the LiveData / StateFlow that the fragment observes.
         */
        /*fun getStaticAttendance(year: Int, month: Int): MonthAttendanceData {
            // Sample data – mirrors what you showed in the screenshot
            val records = listOf(
                AttendanceDayRecord(16, AttendanceStatus.ABSENT),
                AttendanceDayRecord(17, AttendanceStatus.HOLIDAY),
                AttendanceDayRecord(18, AttendanceStatus.PRESENT)
                // Add more days here for testing
            )
            return MonthAttendanceData(
                year = year,
                month = month,
                records = records,
                presentCount = 15,
                totalCount = 20
            )
        }*/

        /**
         * Example mapper — call this when your API response arrives.
         *
         * @param apiRecords  Raw list from API, e.g. [{"day":16,"status":"absent"}, …]
         */
        /*fun mapApiResponse(
            year: Int,
            month: Int,
            presentCount: Int,
            totalCount: Int,
            apiRecords: List<Pair<Int, String>>   // replace Pair<> with your actual API DTO
        ): MonthAttendanceData {
            val records = apiRecords.map { (day, statusStr) ->
                AttendanceDayRecord(
                    day = day,
                    status = when (statusStr.lowercase()) {
                        "present"  -> AttendanceStatus.PRESENT
                        "absent"   -> AttendanceStatus.ABSENT
                        "holiday"  -> AttendanceStatus.HOLIDAY
                        else       -> AttendanceStatus.UNKNOWN
                    }
                )
            }
            return MonthAttendanceData(year, month, records, presentCount, totalCount)
        }*/
    }
}