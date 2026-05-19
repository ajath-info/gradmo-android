package com.app.gradmo.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import com.app.gradmo.model.static.CalenderModels.CalenderModels.AttendanceStatus
import com.app.gradmo.model.static.CalenderModels.CalenderModels.MonthAttendanceData
import java.util.Calendar

/**
 * Custom calendar view that draws a month grid with coloured attendance dots.
 *
 * All colour/status logic lives in [AttendanceStatus] — this view just reads from it.
 *
 * Usage:
 *   binding.calendarView.setMonthData(monthAttendanceData)
 */
class AttendanceCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // ── Colours for non-status text ───────────────────────────────────────────
    private val colorDayDefault      = 0xFF212121.toInt()
    private val colorDayOnDarkCircle = 0xFFFFFFFF.toInt()
    private val colorDayOnLightCircle= 0xFF212121.toInt()  // e.g. yellow background
    private val colorHeader          = 0xFF757575.toInt()

    /**
     * "Light" statuses need dark text on top for readability.
     * Add to this set if you add more pastel/light colours.
     */
    private val lightColorStatuses = setOf(
        AttendanceStatus.HOLIDAY,
        AttendanceStatus.WEEKEND
    )

    // ── Paints ────────────────────────────────────────────────────────────────
    private val circlePaint     = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dayTextPaint    = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER }
    private val headerTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER }

    // ── Layout (computed in onMeasure) ────────────────────────────────────────
    private var cellSize   = 0f
    private var headerRowH = 0f
    private var circleR    = 0f
    private var textSizePx = 0f

    // ── Data ──────────────────────────────────────────────────────────────────
    private var year      = 0
    private var month     = 0     // 1-based
    private var recordMap : Map<Int, AttendanceStatus> = emptyMap()

    private val dayHeaders = listOf("M", "T", "W", "T", "F", "S", "S")

    // ── Public API ────────────────────────────────────────────────────────────

    fun setMonthData(data: MonthAttendanceData) {
        year      = data.year
        month     = data.month
        recordMap = data.recordMap
        requestLayout()
        invalidate()
    }

    // ── Measure ───────────────────────────────────────────────────────────────

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        cellSize    = w / 7f
        headerRowH  = cellSize * 0.6f
        circleR     = cellSize * 0.38f
        textSizePx  = cellSize * 0.30f
        val rows    = weekRowCount()
        setMeasuredDimension(w.toInt(), (headerRowH + rows * cellSize).toInt())
    }

    // ── Draw ──────────────────────────────────────────────────────────────────

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (year == 0) return
        drawHeaders(canvas)
        drawDays(canvas)
    }

    private fun drawHeaders(canvas: Canvas) {
        headerTextPaint.textSize = textSizePx * 0.85f
        headerTextPaint.color    = colorHeader
        headerTextPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        dayHeaders.forEachIndexed { col, label ->
            val cx = col * cellSize + cellSize / 2f
            val cy = headerRowH / 2f - (headerTextPaint.ascent() + headerTextPaint.descent()) / 2f
            canvas.drawText(label, cx, cy, headerTextPaint)
        }
    }

    private fun drawDays(canvas: Canvas) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDow    = cal.get(Calendar.DAY_OF_WEEK)
        val startCol    = (firstDow + 5) % 7   // Mon = 0

        dayTextPaint.textSize = textSizePx
        dayTextPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        var col = startCol
        var row = 0

        for (day in 1..daysInMonth) {
            val cx = col * cellSize + cellSize / 2f
            val cy = headerRowH + row * cellSize + cellSize / 2f

            val status = recordMap[day]

            if (status != null && status.showDot) {
                circlePaint.color = status.color
                canvas.drawCircle(cx, cy, circleR, circlePaint)
                dayTextPaint.color = if (status in lightColorStatuses)
                    colorDayOnLightCircle else colorDayOnDarkCircle
            } else {
                dayTextPaint.color = colorDayDefault
            }

            val textY = cy - (dayTextPaint.ascent() + dayTextPaint.descent()) / 2f
            canvas.drawText(day.toString(), cx, textY, dayTextPaint)

            col++
            if (col == 7) { col = 0; row++ }
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private fun weekRowCount(): Int {
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