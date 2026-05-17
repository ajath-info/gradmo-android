package com.app.gradmo.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import com.app.gradmo.model.static.CalenderModels.AttendanceStatus
import com.app.gradmo.model.static.CalenderModels.MonthAttendanceData
import java.util.Calendar

/**
 * A self-contained calendar view that draws a month grid and overlays
 * coloured circles for each day that has an attendance record.
 *
 * Usage in XML:
 *
 *   <com.yourpackage.ui.attendance.AttendanceCalendarView
 *       android:id="@+id/calendarView"
 *       android:layout_width="match_parent"
 *       android:layout_height="wrap_content" />
 *
 * Then in your Fragment:
 *
 *   binding.calendarView.setMonthData(attendanceData)
 */
class AttendanceCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // ── Colours ───────────────────────────────────────────────────────────────
    private val colorPresent  = 0xFF4CAF50.toInt()  // green
    private val colorAbsent   = 0xFFE53935.toInt()  // red
    private val colorHoliday  = 0xFFFFEB3B.toInt()  // yellow
    private val colorUnknown  = 0xFF9E9E9E.toInt()  // grey

    private val colorDayText         = 0xFF212121.toInt()
    private val colorDayTextOnCircle = 0xFFFFFFFF.toInt()
    private val colorDayTextHoliday  = 0xFF212121.toInt()  // yellow bg → dark text
    private val colorHeaderText      = 0xFF757575.toInt()
    private val colorOtherMonth      = 0xFFBDBDBD.toInt()

    // ── Paints ────────────────────────────────────────────────────────────────
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dayTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }
    private val headerTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface  = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }

    // ── Layout constants (calculated in onMeasure) ────────────────────────────
    private var cellSize   = 0f
    private var headerRowH = 0f
    private var circleR    = 0f
    private var textSizePx = 0f

    // ── Data ──────────────────────────────────────────────────────────────────
    private var year  = 0
    private var month = 0   // 1-based
    private var recordMap: Map<Int, AttendanceStatus> = emptyMap()

    // Week starts on Monday (index 0 = Mon … 6 = Sun)
    private val dayHeaders = listOf("M", "T", "W", "T", "F", "S", "S")

    // ── Public API ────────────────────────────────────────────────────────────

    fun setMonthData(data: MonthAttendanceData) {
        year      = data.year
        month     = data.month
        recordMap = data.recordMap
        requestLayout()
        invalidate()
    }

    // ── Measurement ───────────────────────────────────────────────────────────

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        cellSize    = w / 7f
        headerRowH  = cellSize * 0.6f
        circleR     = cellSize * 0.38f
        textSizePx  = cellSize * 0.30f

        val rows = getWeekRows()
        val h    = (headerRowH + rows * cellSize).toInt()
        setMeasuredDimension(w.toInt(), h)
    }

    // ── Drawing ───────────────────────────────────────────────────────────────

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (year == 0) return

        drawDayHeaders(canvas)
        drawDayCells(canvas)
    }

    private fun drawDayHeaders(canvas: Canvas) {
        headerTextPaint.textSize  = textSizePx * 0.9f
        headerTextPaint.color     = colorHeaderText
        headerTextPaint.typeface  = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        dayHeaders.forEachIndexed { col, label ->
            val cx = col * cellSize + cellSize / 2f
            val cy = headerRowH / 2f - (headerTextPaint.ascent() + headerTextPaint.descent()) / 2f
            canvas.drawText(label, cx, cy, headerTextPaint)
        }
    }

    private fun drawDayCells(canvas: Canvas) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)   // Calendar is 0-based
            set(Calendar.DAY_OF_MONTH, 1)
        }

        // How many days in this month
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Day of week for the 1st (Calendar.SUNDAY=1 … SATURDAY=7) → map to Mon=0
        val firstDow = cal.get(Calendar.DAY_OF_WEEK)
        val startCol = (firstDow + 5) % 7   // Mon-start offset

        dayTextPaint.textSize = textSizePx

        var col = startCol
        var row = 0

        for (day in 1..daysInMonth) {
            val cx = col * cellSize + cellSize / 2f
            val cy = headerRowH + row * cellSize + cellSize / 2f

            val status = recordMap[day]

            if (status != null) {
                // Draw coloured circle
                circlePaint.color = statusColor(status)
                canvas.drawCircle(cx, cy, circleR, circlePaint)

                // Text on top of circle
                dayTextPaint.color = if (status == AttendanceStatus.HOLIDAY)
                    colorDayTextHoliday else colorDayTextOnCircle
            } else {
                dayTextPaint.color = colorDayText
            }

            // Draw day number
            val textY = cy - (dayTextPaint.ascent() + dayTextPaint.descent()) / 2f
            canvas.drawText(day.toString(), cx, textY, dayTextPaint)

            col++
            if (col == 7) { col = 0; row++ }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun statusColor(status: AttendanceStatus) = when (status) {
        AttendanceStatus.PRESENT  -> colorPresent
        AttendanceStatus.ABSENT   -> colorAbsent
        AttendanceStatus.HOLIDAY  -> colorHoliday
        AttendanceStatus.UNKNOWN  -> colorUnknown
    }

    private fun getWeekRows(): Int {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDow    = cal.get(Calendar.DAY_OF_WEEK)
        val startCol    = (firstDow + 5) % 7
        return Math.ceil((startCol + daysInMonth) / 7.0).toInt()
    }
}
