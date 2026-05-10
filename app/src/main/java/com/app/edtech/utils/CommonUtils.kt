package com.app.edtech.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.app.edtech.R
import com.app.edtech.databinding.FragmentFilterInstituteBottomSheetBinding
import java.text.SimpleDateFormat
import java.util.*

object CommonUtils {

    fun convertTimeFormat(
        inputTime: String,
        inputFormat: String,
        outputFormat: String
    ): String? {
        return try {
            val inputSdf = SimpleDateFormat(inputFormat, Locale.getDefault())
            val outputSdf = SimpleDateFormat(outputFormat, Locale.getDefault())

            val date = inputSdf.parse(inputTime)
            outputSdf.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    fun updateSortUI(
        binding: FragmentFilterInstituteBottomSheetBinding,
        isAsc: Boolean
    ) {
        binding.tvAZ.setCompoundDrawablesWithIntrinsicBounds(
            if (isAsc) R.drawable.selected_icon else R.drawable.unselected_icon,
            0, 0, 0
        )

        binding.tvZA.setCompoundDrawablesWithIntrinsicBounds(
            if (!isAsc) R.drawable.selected_icon else R.drawable.unselected_icon,
            0, 0, 0
        )
    }

    fun updateModeUI(
        binding: FragmentFilterInstituteBottomSheetBinding,
        isOffline: Boolean
    ) {
        binding.tvOffline.setCompoundDrawablesWithIntrinsicBounds(
            if (isOffline) R.drawable.selected_icon else R.drawable.unselected_icon,
            0, 0, 0
        )

        binding.tvOnline.setCompoundDrawablesWithIntrinsicBounds(
            if (!isOffline) R.drawable.selected_icon else R.drawable.unselected_icon,
            0, 0, 0
        )
    }

    fun Int.dpToPx(context: Context): Int =
        (this * context.resources.displayMetrics.density).toInt()
    @SuppressLint("ClickableViewAccessibility")
    fun touchHideKeyBoard(view: View, activity: Activity){
        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val focusedView = activity.currentFocus
                if (focusedView is EditText) {
                    val outRect = Rect()
                    focusedView.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        focusedView.clearFocus()
                        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
                    }
                }
            }
            false
        }
    }
    object DotPasswordTransformationMethod : PasswordTransformationMethod() {
        override fun getTransformation(source: CharSequence, view: View): CharSequence {
            return PasswordCharSequence(super.getTransformation(source, view))
        }

        private class PasswordCharSequence(
            val transformation: CharSequence
        ) : CharSequence by transformation {
            override fun get(index: Int): Char = if (transformation[index] == DOT) {
                STAR
            } else {
                transformation[index]
            }
        }

        private const val DOT = '\u2022'
        private const val STAR = '*'
    }
    fun hideKeyboard(activity: Activity) {
        Log.i("TAG", "hideKeyboard: ")
        try {
            val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputMethodManager.isActive) {
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
            } else
                return
        } catch (e: Exception) {

        }
    }
    fun isValidEmail(email: String?): Boolean {
        return !email.isNullOrEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}