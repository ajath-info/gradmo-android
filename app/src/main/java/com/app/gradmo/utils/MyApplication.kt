package com.app.gradmo.utils

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import dagger.hilt.android.HiltAndroidApp
import us.zoom.sdk.ZoomVideoSDK
import us.zoom.sdk.ZoomVideoSDKInitParams

@HiltAndroidApp
class MyApplication : Application(), LifecycleObserver {

    companion object {
        var appContext: Context? = null
    }

//    private val apiRepository = ApiRepository()
//    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appContext = this
        initZoomSDK()
//        registerPhoneAccount(this)
        // Observe app background/foreground
//        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }
    private fun initZoomSDK() {
        val initParams = ZoomVideoSDKInitParams().apply {
            domain = "zoom.us"
        }

        val sdk = ZoomVideoSDK.getInstance()
        val initResult = sdk.initialize(this, initParams)
        Log.d("ZoomSDK", "Init result: $initResult")
    }


}
