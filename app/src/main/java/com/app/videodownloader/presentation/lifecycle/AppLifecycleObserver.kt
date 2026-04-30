package com.app.videodownloader.presentation.lifecycle

import android.app.Activity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.app.videodownloader.domain.repository.ads.AdManager

class AppLifecycleObserver(
    private val adManager: AdManager
) : DefaultLifecycleObserver {

    private var currentActivity: Activity? = null

    fun setCurrentActivity(activity: Activity) {
        currentActivity = activity
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)

        currentActivity?.let {
            adManager.showAd(it)
        }

        adManager.loadAd()
    }
}