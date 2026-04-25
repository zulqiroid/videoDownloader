package com.app.videodownloader.core

import android.app.Application
import com.app.videodownloader.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                appModule
            )
        }
    }
}