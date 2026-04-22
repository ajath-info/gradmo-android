package com.app.edtech.ui.signup.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.app.edtech.R
import com.app.edtech.base.BaseActivity
import com.app.edtech.databinding.ActivitySignupFlowBinding

class SignupFlowActivity : BaseActivity<ActivitySignupFlowBinding>() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.signup_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val startFrom = intent.getStringExtra("start_from")

        val navGraph = navController.navInflater.inflate(R.navigation.signup_flow_nav)
//        var bundle = Bundle()
//        bundle.putString("from", "signup")
        when (startFrom) {
            "incompleteProfile" -> navGraph.setStartDestination(R.id.editProfileNewFragment)
            else -> navGraph.setStartDestination(R.id.onboardingFragment)
        }

        navController.graph = navGraph
    }

    fun isGestureNavigation(): Boolean {
        val resId = resources.getIdentifier("config_navBarInteractionMode", "integer", "android")
        return resId > 0 && resources.getInteger(resId) == 2
    }
    override fun getLayoutId(): Int {
        return R.layout.activity_signup_flow // Ensure this points to the correct layout resource
    }
}