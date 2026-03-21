package com.app.edtech.utils

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleObserver
import dagger.hilt.android.HiltAndroidApp

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
//        registerPhoneAccount(this)
        // Observe app background/foreground
//        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }


}
