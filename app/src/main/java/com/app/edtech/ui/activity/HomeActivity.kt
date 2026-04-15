package com.app.edtech.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.edtech.R
import com.app.edtech.base.BaseActivity
import com.app.edtech.databinding.ActivityHomeBinding
import com.app.edtech.databinding.LayoutMenuBottomSheetBinding
import com.app.edtech.model.login.response.LoginResponse
import com.app.edtech.model.login.response.UserData
import com.app.edtech.model.menu_item.MenuItemModel
import com.app.edtech.preferences.FCM_TOKEN
import com.app.edtech.preferences.IS_FIRST_LOGIN_DONE
import com.app.edtech.preferences.LOGIN_DATA
import com.app.edtech.preferences.Preferences
import com.app.edtech.preferences.UserPreference
import com.app.edtech.ui.adapter.MenuAdapter
import com.app.edtech.ui.signup.activity.SignupFlowActivity
import com.app.edtech.ui.signup.view_model.HomeActivityViewModel
import com.app.edtech.utils.CommonDialog
import com.app.edtech.utils.network_utils.ProcessDialog
import com.app.edtech.utils.network_utils.Status
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlin.text.ifEmpty

class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    private lateinit var navController: NavController
    private val CAMERA_MIC_PERMISSION_REQUEST_CODE = 100
    private val OVERLAY_PERMISSION_REQ_CODE = 123
    private lateinit var sideMenuBinding: LayoutMenuBottomSheetBinding
    private val viewModel: HomeActivityViewModel by viewModels()
    private var userData = UserData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        sideMenuBinding = binding.sideMenu
        userData = Preferences.getCustomModelPreference<LoginResponse>(this, LOGIN_DATA)?.data ?: UserData()
        observeApis()
        floatingButtonClick()
        navigationMenuClickListener()
//        setBottomBarPadding()
        setBottomNavigation()
        fragmentChangeCallback()
//        handleActivityBackButton()
//        requestCameraAndMicrophonePermissions()
        Handler(Looper.getMainLooper()).post {
            handleIntentNavigation(intent)
        }
        initMenu()

    }
    private fun observeApis() {

        viewModel.getDeleteAccountLiveData().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "resend otp: ${Gson().toJson(it)}")
                    if (it.data?.status==true){
                        Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                        performLogout()
                    }else{
                        Toast.makeText(this, "${it.data?.message}", Toast.LENGTH_SHORT).show()
                    }
                    ProcessDialog.dismissDialog(true)
                }
                Status.LOADING -> {
                    ProcessDialog.showDialog(this, true)
                }
                Status.ERROR -> {
                    Log.e("TAG", "Login Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
        viewModel.getLogoutLiveData().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "resend otp: ${Gson().toJson(it)}")
                    if (it.data?.status=="true"){
                        Toast.makeText(this, it.data.msg, Toast.LENGTH_SHORT).show()
                        performLogout()
                    }else{
                        Toast.makeText(this, "${it.data?.msg}", Toast.LENGTH_SHORT).show()
                    }
                    ProcessDialog.dismissDialog(true)
                }
                Status.LOADING -> {
                    ProcessDialog.showDialog(this, true)
                }
                Status.ERROR -> {
                    Log.e("TAG", "Login Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
    }
    private fun initMenu() {
        val menuList = listOf(
            MenuItemModel(R.drawable.home_menu, "Home"),
            MenuItemModel(R.drawable.menu_batch, "My Batches"),
            MenuItemModel(R.drawable.menu_profile, "Edit Profile"),
            MenuItemModel(R.drawable.payment_history_menu, "Payment History"),
            MenuItemModel(R.drawable.privacy_policy_menu, "Privacy Policy"),
            MenuItemModel(R.drawable.about_menu, "About App"),
            MenuItemModel(R.drawable.logout_menu, "Logout"),
            MenuItemModel(R.drawable.delete_account_menu, "Delete Account"),
            MenuItemModel(R.drawable.share_menu, "Share App"),
            MenuItemModel(R.drawable.update_password_menu, "Update Password"),

        )

        binding.sideMenu.recyclerMenu.layoutManager = LinearLayoutManager(this)
        sideMenuBinding.txtName.text = Preferences.getCustomModelPreference<LoginResponse>(this, LOGIN_DATA)?.data?.name
        Glide.with(this).load(Preferences.getCustomModelPreference<LoginResponse>(this, LOGIN_DATA)?.data?.image).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).into(sideMenuBinding.imgProfile)
        sideMenuBinding.recyclerMenu.adapter = MenuAdapter(menuList) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            when (it.title) {
                "Home" -> {

                }

                "My Batches" -> {

                }

                "Edit Profile" -> {

                }

                "Payment History" -> {

                }

                "Privacy Policy" -> {

                }

                "About App" -> {

                }

                "Logout" -> {
                    logOutDialog()
                }

                "Delete Account" -> {
                    deleteAccountDialog()
                }

                "Share App" -> {

                }

                "Update Password" -> {

                }
            }
        }
    }

    private fun deleteAccountDialog() {
        CommonDialog(
            context = this,
            title = "Are you sure you want to delete your account permanently?",
            imageRes = R.drawable.ic_warning,
            positiveText = "Delete",
            negativeText = "Cancel",
            onPositiveClick = {
                val studentId = UserPreference.studentId.ifEmpty { Preferences.getCustomModelPreference<LoginResponse>(this, LOGIN_DATA)?.data?.studentId }
                val accessToken = Preferences.getCustomModelPreference<LoginResponse>(this, LOGIN_DATA)?.data?.accessToken
                viewModel.hitDeleteAccountApi("Bearer $accessToken", studentId.toString())
            }
        ).show()
    }
    fun logOutDialog() {
        CommonDialog(
            context = this,
            title = "Do you want to logout?",
            imageRes = R.drawable.logout_menu,
            positiveText = "Logout",
            negativeText = "Cancel",
            onPositiveClick = {
                val studentId = UserPreference.studentId.ifEmpty { Preferences.getCustomModelPreference<LoginResponse>(this, LOGIN_DATA)?.data?.studentId }
                val accessToken = Preferences.getCustomModelPreference<LoginResponse>(this, LOGIN_DATA)?.data?.accessToken
                viewModel.hitLogoutApi("Bearer $accessToken", studentId.toString())
            }
        ).show()
    }
    fun performLogout() {
        Preferences.removeAllPreferencesExcept(this, listOf(FCM_TOKEN, IS_FIRST_LOGIN_DONE))
        val intent = Intent(this, SignupFlowActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntentNavigation(intent)

    }
    fun updateUserInMenu(){
        userData = Preferences.getCustomModelPreference<LoginResponse>(this, LOGIN_DATA)?.data ?: UserData()
        sideMenuBinding.apply {
            txtName.text = userData.name
            Glide.with(this@HomeActivity).load(userData.image).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).into(imgProfile)
        }
    }
    private fun handleIntentNavigation(intent: Intent) {
        /*val target = intent.getStringExtra("target_fragment")
        Log.i("TAG", "handleIntentNavigation: "+target)
        when (target) {
            "message" -> {
                Log.i("TAG", "handleIntentNavigation chatid: "+intent.getStringExtra("chatId") ?: "")
                UserPreference.CHAT_PUSH_NOTIFICATION_ID = intent.getStringExtra("chat_id") ?: ""
                navController.navigate(R.id.chatListFragment)
            }
            "search" -> navController.navigate(R.id.searchFragment)
            "profile" -> navController.navigate(R.id.profileFragment)
            "reels" -> navController.navigate(R.id.reelsFragment)
            "home" -> navController.navigate(R.id.homeFragment)
        }*/
    }
    private fun handleActivityBackButton() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentDestinationId = navController.currentDestination?.id

                when (currentDestinationId) {
//
                }
            }
        })
    }

    private fun popBackToHome() {
//        val popped = navController.popBackStack(R.id.homeFragment, false)
//        if (!popped) {
//            // HomeFragment not in back stack — navigate to it
//            navController.navigate(R.id.homeFragment)
//        }
//        binding.bottomNavigationView.selectedItemId = R.id.home
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_home // Ensure this points to the correct layout resource
    }
    private fun floatingButtonClick() {
        binding.floatingbtn.setOnClickListener {
            binding.bottomNavigationView.selectedItemId = R.id.search
        }
    }
    private fun clearBottomBarPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root, null)
        binding.bottomAppBar.setPadding(0, 0, 0, 0)
        binding.bottomNavigationView.setPadding(0, 0, 0, 0)
    }

    fun setBottomBarPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Remove extra padding from BottomAppBar and its children
            binding.bottomAppBar.setPadding(0, 0, 0, 0)
            val offset = if (isGestureNavigation()) systemBars.bottom else 0
            binding.bottomNavigationView.setPadding(0, 0, 0, offset)
            WindowInsetsCompat.CONSUMED
        }
    }
    /*fun fullyResetFloatingButton() {
       val root = findViewById<ConstraintLayout>(R.id.home)
       val fab = findViewById<FloatingActionButton>(R.id.floatingbtn)
       val img = findViewById<AppCompatImageView>(R.id.imgBtn)

       // Temporarily remove both
       root.removeView(fab)
       root.removeView(img)

       // Re-add them after a slight delay to allow layout to settle
       Handler(Looper.getMainLooper()).postDelayed({
           root.addView(fab)
           root.addView(img)
       }, 100)
   }*/


    fun isGestureNavigation(): Boolean {
        val resId = resources.getIdentifier("config_navBarInteractionMode", "integer", "android")
        return resId > 0 && resources.getInteger(resId) == 2
    }
    private fun setBottomNavigation() {
        binding.bottomNavigationView.background = null
//        binding.bottomNavigationView.selectedItemId = R.id.home
    }
    private fun navigationMenuClickListener() {
//        binding.bottomNavigationView.itemIconTintList = null
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val currentDestId = navController.currentDestination?.id
            when (item.itemId) {
                R.id.home -> {
//                    if (currentDestId != R.id.homeFragment) {
                        navController.navigate(R.id.homeFragment)
//                    }else{
//                        supportFragmentManager.setFragmentResult("home_click", Bundle())
//                    }
                    binding.imgBtn.setImageResource(R.drawable.search_icon_menu)
                    true
                }
                R.id.course -> {
//                    if (currentDestId != R.id.chatListFragment) {
                    navController.navigate(R.id.courseFragment)
//                    }
                    binding.imgBtn.setImageResource(R.drawable.search_icon_menu)
                    true
                }
                R.id.search -> {
//                    if (currentDestId != R.id.reelsFragment) {
                    navController.navigate(R.id.searchFragment)
//                    }
                    binding.imgBtn.setImageResource(R.drawable.search_icon_menu)
                    true
                }
                R.id.notification -> {
                    if (currentDestId != R.id.notificationFragment) {
                        navController.navigate(R.id.notificationFragment)
                    }
                    binding.imgBtn.setImageResource(R.drawable.search_icon_menu)
                     true
                }
                R.id.account -> {
                    if (currentDestId != R.id.editProfileNewFragment) {
                        navController.navigate(R.id.editProfileNewFragment)
                    }
                    binding.imgBtn.setImageResource(R.drawable.search_icon_menu)
                    true
                }
                else -> false
            }
        }

    }
     fun navigateToHome() {
//        if (currentDestId != R.id.profileFragment) {
        navController.navigate(R.id.homeFragment)
//        }
//         binding.bottomNavigationView.menu.findItem(R.id.home).icon = ContextCompat.getDrawable(this, R.drawable.home_icon)
        binding.imgBtn.setImageResource(R.drawable.search_icon_menu)
         binding.bottomNavigationView.selectedItemId = R.id.home
        // Load profile image with stroke
    }

    private fun fragmentChangeCallback() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.homeFragment, R.id.courseFragment, R.id.notificationFragment ->{
                    binding.bottomAppBar.isVisible=true
                    binding.floatingbtn.isVisible=true
                    binding.imgBtn.isVisible=true
                }

                else -> {
                    binding.bottomAppBar.isVisible=false
                    binding.floatingbtn.isVisible=false
                    binding.imgBtn.isVisible=false
                }
//                R.id.profileFragment, R.id.chatListFragment, R.id.searchFragment -> {
//                    showNavigationView()
//                    setBottomBarPadding()
//                }
//                R.id.homeFragment -> {
//                    binding.bottomNavigationView.menu.findItem(R.id.home).icon = ContextCompat.getDrawable(this, R.drawable.home_selected)
//                    setUserProfileImageWithStroke(this, binding.bottomNavigationView, userImageUrl, isSelected = false)
//                    showNavigationView()
//                    setBottomBarPadding()
//                }
//                R.id.reelsFragment -> {
////                    binding.imgBtn.setImageResource(R.drawable.reel_icon_selected)
//                    showNavigationView()
//                    setBottomBarPadding()
//                    if (UserPreference.navigatedToMyProfile){
//                        binding.bottomNavigationView.menu.findItem(R.id.home).icon = ContextCompat.getDrawable(this, R.drawable.home_icon)
//                        setUserProfileImageWithStroke(this, binding.bottomNavigationView, userImageUrl, isSelected = true)
//                    }
//                }
//                R.id.chatFragment, R.id.newStoryFragment, R.id.predefinedChatFragment, R.id.addReelFragment, R.id.editProfileNewFragment, R.id.storyFragment, R.id.secondStoryFragment, R.id.becomeCreatorStatusFragment, R.id.benifitsOfCreatersFragment, R.id.rateUsFragment, R.id.openImageFragment, R.id.changePasswordFragment  -> {
//                    clearBottomBarPadding()
//                    hideNavigationView()
//
//                }
//                else -> {
//                    setBottomBarPadding()
//                    showNavigationView()
//                }
            }
        }
    }

    private fun requestCameraAndMicrophonePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 (API 33) and above
            val requiredPermissions = mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.POST_NOTIFICATIONS
            )

            // Bluetooth for calls
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requiredPermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
                requiredPermissions.add(Manifest.permission.BLUETOOTH_SCAN)
            }

            val missingPermissions = requiredPermissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }

            if (missingPermissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    missingPermissions.toTypedArray(),
                    CAMERA_MIC_PERMISSION_REQUEST_CODE
                )
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 (API 31–32)
            val requiredPermissions = mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )

            val missingPermissions = requiredPermissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }

            if (missingPermissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    missingPermissions.toTypedArray(),
                    CAMERA_MIC_PERMISSION_REQUEST_CODE
                )
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6 (API 23) to Android 11 (API 30)
            val requiredPermissions = listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
                // BLUETOOTH & BLUETOOTH_ADMIN are normal permissions below Android 12,
                // so no runtime request needed
            )

            val missingPermissions = requiredPermissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }

            if (missingPermissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    missingPermissions.toTypedArray(),
                    CAMERA_MIC_PERMISSION_REQUEST_CODE
                )
            }
        }

        // 🔹 Overlay permission check (same as your code)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            requestOverlayPermission()
        }
    }

    private fun requestOverlayPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_MIC_PERMISSION_REQUEST_CODE) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (allGranted) {
                // All permissions granted
            } else {
                Toast.makeText(this, "Permissions required for video call!", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                // Overlay permission granted
            } else {
                Toast.makeText(this, "Permissions required for video call!", Toast.LENGTH_SHORT).show()
                requestOverlayPermission()
            }
        }
    }



}

