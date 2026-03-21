package com.app.hihlo.ui.signup.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.app.edtech.R
import com.app.edtech.base.BaseActivity
import com.app.edtech.databinding.ActivityMainBinding
import com.app.edtech.databinding.ActivitySignupFlowBinding
import com.app.edtech.preferences.IS_LOGIN
import com.app.edtech.preferences.LOGIN_DATA
import com.app.edtech.preferences.Preferences

class SignupFlowActivity : BaseActivity<ActivitySignupFlowBinding>() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.signup_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    fun isGestureNavigation(): Boolean {
        val resId = resources.getIdentifier("config_navBarInteractionMode", "integer", "android")
        return resId > 0 && resources.getInteger(resId) == 2
    }
    override fun getLayoutId(): Int {
        return R.layout.activity_signup_flow // Ensure this points to the correct layout resource
    }
}