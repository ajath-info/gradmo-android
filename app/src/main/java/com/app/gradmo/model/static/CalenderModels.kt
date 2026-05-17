package com.app.gradmo.model.static

class CalenderModels {
    // ─── Data Models ───────────────────────────────────────────────────────────

    /**
     * Represents the attendance status for a single day.
     * Add more statuses here as new ones arrive from the API.
     */
    enum class AttendanceStatus {
        PRESENT,
        ABSENT,
        HOLIDAY,
        UNKNOWN  // fallback for any new status from API
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

    /**
     * Full attendance data for one month.
     * @param year   e.g. 2026
     * @param month  1-based (1 = January … 12 = December)
     * @param records  List of day records. Days not present in this list are shown with no colour.
     * @param presentCount  Numerator shown in summary (e.g. 15)
     * @param totalCount    Denominator shown in summary (e.g. 20)
     */
    data class MonthAttendanceData(
        val year: Int,
        val month: Int,
        val records: List<AttendanceDayRecord>,
        val presentCount: Int,
        val totalCount: Int
    ) {
        val percentage: Int get() = if (totalCount > 0) (presentCount * 100) / totalCount else 0

        /** Quick lookup: day → status */
        val recordMap: Map<Int, AttendanceStatus> by lazy {
            records.associate { it.day to it.status }
        }
    }


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
        fun getStaticAttendance(year: Int, month: Int): MonthAttendanceData {
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
        }

        /**
         * Example mapper — call this when your API response arrives.
         *
         * @param apiRecords  Raw list from API, e.g. [{"day":16,"status":"absent"}, …]
         */
        fun mapApiResponse(
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
        }
    }
}