package com.app.videodownloader.core

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.app.videodownloader.data.manager.AppOpenAdManager
import com.app.videodownloader.di.appModule
import com.app.videodownloader.presentation.lifecycle.AppLifecycleObserver
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application(){


    override fun onCreate() {
        super.onCreate()
        startKoin {
            printLogger()
            androidContext(this@App)
            modules(
                appModule
            )
        }

        MobileAds.initialize(this@App) {
            Log.d("AppOpenAd", "it is initaializiing")
        }
    }
}