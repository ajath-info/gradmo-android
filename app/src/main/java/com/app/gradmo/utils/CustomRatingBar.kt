package com.app.gradmo.utils

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.LinearLayout
import com.app.gradmo.R

class CustomRatingBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_STARS = 5
        private const val DEFAULT_STAR_SIZE_DP = 32
        private const val DEFAULT_STAR_SPACING_DP = 6
    }

    private var totalStars: Int = DEFAULT_STARS
    private var currentRating: Float = 0f
    private var starSizePx: Int = 0
    private var starSpacingPx: Int = 0
    private var isIndicator: Boolean = false
    private var stepSize: Float = 1f

    var onRatingChanged: ((Float) -> Unit)? = null

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        starSizePx = DEFAULT_STAR_SIZE_DP.dpToPx()
        starSpacingPx = DEFAULT_STAR_SPACING_DP.dpToPx()

        context.theme.obtainStyledAttributes(attrs, R.styleable.CustomRatingBar, 0, 0).apply {
            try {
                totalStars = getInteger(R.styleable.CustomRatingBar_numStars, DEFAULT_STARS)
                currentRating = getFloat(R.styleable.CustomRatingBar_rating, 0f)
                isIndicator = getBoolean(R.styleable.CustomRatingBar_isIndicator, false)
                stepSize = getFloat(R.styleable.CustomRatingBar_stepSize, 1f)
                starSizePx = getDimensionPixelSize(
                    R.styleable.CustomRatingBar_starSize,
                    DEFAULT_STAR_SIZE_DP.dpToPx()
                )
                starSpacingPx = getDimensionPixelSize(
                    R.styleable.CustomRatingBar_starSpacing,
                    DEFAULT_STAR_SPACING_DP.dpToPx()
                )
            } finally {
                recycle()
            }
        }
        buildStars()
    }

    private fun buildStars() {
        removeAllViews()
        for (i in 1..totalStars) {
            val star = ImageView(context).apply {
                layoutParams = LayoutParams(starSizePx, starSizePx).also { params ->
                    if (i > 1) params.marginStart = starSpacingPx
                }
                scaleType = ImageView.ScaleType.FIT_CENTER
                tag = i
            }
            addView(star)
        }
        refreshStars()
        if (!isIndicator) setupTouchListener()
    }

    private fun refreshStars() {
        for (i in 0 until childCount) {
            val star = getChildAt(i) as? ImageView ?: continue
            val starIndex = i + 1
            val drawable = when {
                starIndex <= currentRating.toInt() -> R.drawable.star_filled   // fully filled
                starIndex == currentRating.toInt() + 1 && (currentRating % 1 >= 0.5f) ->
                    R.drawable.star_filled   // half → treat as filled for simplicity
                else -> R.drawable.star_empty                                 // hollow
            }
            star.setImageResource(drawable)
        }
    }

    private fun setupTouchListener() {
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_MOVE) {
                val rawRating = (event.x / width.toFloat()) * totalStars
                val snapped = snapToStep(rawRating.coerceIn(0f, totalStars.toFloat()))
                if (snapped != currentRating) {
                    currentRating = snapped
                    refreshStars()
                    onRatingChanged?.invoke(currentRating)
                }
            }
            true
        }
    }

    private fun snapToStep(raw: Float): Float {
        return (Math.round(raw / stepSize) * stepSize)
            .coerceIn(0f, totalStars.toFloat())
    }

    fun setRating(rating: Float) {
        currentRating = rating.coerceIn(0f, totalStars.toFloat())
        refreshStars()
    }

    fun getRating(): Float = currentRating

    fun setIsIndicator(indicator: Boolean) {
        isIndicator = indicator
        if (indicator) setOnTouchListener(null) else setupTouchListener()
    }

    private fun Int.dpToPx(): Int =
        (this * context.resources.displayMetrics.density).toInt()
}