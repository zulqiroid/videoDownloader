package com.app.videodownloader.core

import android.app.Application
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import com.app.videodownloader.di.appModule
import com.app.videodownloader.domain.repository.RemoteConfigRepository
import com.app.videodownloader.presentation.lifecycle.AppOpenAdLifecycleObserver
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class App : Application() {

    private lateinit var appOpenAdLifecycleObserver: AppOpenAdLifecycleObserver

    override fun onCreate() {
        super.onCreate()

        startKoin {
            printLogger()
            androidContext(this@App)
            modules(appModule)
        }

        val koin = GlobalContext.get()

        appOpenAdLifecycleObserver = koin.get()

        registerActivityLifecycleCallbacks(appOpenAdLifecycleObserver)

        ProcessLifecycleOwner
            .get()
            .lifecycle
            .addObserver(appOpenAdLifecycleObserver)

        CoroutineScope(Dispatchers.IO).launch {
            val remoteConfigRepository: RemoteConfigRepository = koin.get()

            remoteConfigRepository.initializeAndFetch {
                MobileAds.initialize(this@App) {
                    Log.d(TAG, "Google Mobile Ads SDK initialized.")

                    CoroutineScope(Dispatchers.Main).launch {
                        appOpenAdLifecycleObserver.preload()
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "App"
    }
}