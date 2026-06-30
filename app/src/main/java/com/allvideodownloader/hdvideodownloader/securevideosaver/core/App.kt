package com.allvideodownloader.hdvideodownloader.securevideosaver.core

import android.app.Application
import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import com.allvideodownloader.hdvideodownloader.securevideosaver.di.appModule
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.RemoteConfigRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.notification.AppNotificationManager
import com.allvideodownloader.hdvideodownloader.securevideosaver.framework.push.FcmTopicManager
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.lifecycle.AppOpenAdLifecycleObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    private val applicationScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    private lateinit var appOpenAdLifecycleObserver: AppOpenAdLifecycleObserver

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()

        initializeDependencyInjection()
        initializeNotificationChannels()
        initializeFcmTopics()
        initializeAppOpenAdsLifecycle()
        initializeRemoteConfig()
    }

    private fun initializeDependencyInjection() {
        startKoin {
            printLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }

    private fun initializeNotificationChannels() {
        get<AppNotificationManager>()
            .createNotificationChannels()
    }

    private fun initializeFcmTopics() {
        get<FcmTopicManager>()
            .subscribeToDefaultTopics()
    }

    private fun initializeAppOpenAdsLifecycle() {
        appOpenAdLifecycleObserver = get()

        registerActivityLifecycleCallbacks(appOpenAdLifecycleObserver)

        ProcessLifecycleOwner
            .get()
            .lifecycle
            .addObserver(appOpenAdLifecycleObserver)
    }

    private fun initializeRemoteConfig() {
        applicationScope.launch {
            val remoteConfigRepository: RemoteConfigRepository = get()

            remoteConfigRepository.initializeAndFetch {
                /*
                 * Remote Config is ready.
                 */
            }
        }
    }
}