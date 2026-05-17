package com.app.gradmo.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.app.gradmo.R

open class BaseActivity<DB : ViewDataBinding> : AppCompatActivity() {

    private var _binding: DB? = null
    protected val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, getLayoutId())
        binding.lifecycleOwner = this
        setTransparentStatusBar()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    open fun getLayoutId(): Int {
        // Override this method in your Activity to return the correct layout ID.
        return 0 // Default: you should override this
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setTransparentStatusBar() {
        window.apply {
            statusBarColor = Color.TRANSPARENT
            window.decorView.setBackgroundColor(ContextCompat.getColor(context, R.color.black))

            // Enable edge-to-edge mode
            WindowCompat.setDecorFitsSystemWindows(this, false)

            // Apply window insets listener
            decorView.setOnApplyWindowInsetsListener { view, insets ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                    // Check if 3-button navigation is enabled
                    if (isThreeButtonNavigation()) {
                        view.setPadding(0, 0, 0, systemBarsInsets.bottom) // Add padding only for 3-button mode
                    } else {
                        view.setPadding(0, 0, 0, 0) // No extra padding in gesture mode
                    }
                } else {
                    // Android 10 and below
                    @Suppress("DEPRECATION")
                    view.setPadding(
                        insets.systemWindowInsetLeft,
                        insets.systemWindowInsetTop,
                        insets.systemWindowInsetRight,
                        insets.systemWindowInsetBottom
                    )
                }

                insets
            }

            updateStatusBarIcons(isColorLight(window.decorView.backgroundColor()))
        }
    }
    private fun isThreeButtonNavigation(): Boolean {
        val resourceId = resources.getIdentifier("config_navBarInteractionMode", "integer", "android")
        return if (resourceId > 0) {
            resources.getInteger(resourceId) == 0  // 0 = 3-button navigation mode
        } else {
            false // Default to false (gesture mode)
        }
    }




    fun updateStatusBarIcons(isLightBackground: Boolean) {
        // Use WindowInsetsControllerCompat to adjust the status bar icon appearance
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = isLightBackground
            isAppearanceLightNavigationBars = isLightBackground
        }
    }

    fun isColorLight(color: Int): Boolean {
        val darkness = 1 - (0.299 * ((color shr 16) and 0xFF) +
                0.587 * ((color shr 8) and 0xFF) +
                0.114 * (color and 0xFF)) / 255
        return darkness < 0.5
    }

    // Extension function to get the background color of the decorView
    private fun View.backgroundColor(): Int {
        val background = background
        return if (background is ColorDrawable) {
            background.color
        } else {
            Color.WHITE // Default color if no background is set
        }
    }
}
