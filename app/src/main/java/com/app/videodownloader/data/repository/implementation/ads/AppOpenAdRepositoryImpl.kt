package com.app.videodownloader.data.repository.implementation.ads

import android.app.Activity
import com.app.videodownloader.data.manager.AppOpenAdManager
import com.app.videodownloader.domain.model.ads.AdState
import com.app.videodownloader.domain.model.ads.AppOpenAdConfig
import com.app.videodownloader.domain.repository.ads.AppOpenAdRepository

class AppOpenAdRepositoryImpl(
    private val appOpenAdManager: AppOpenAdManager
) : AppOpenAdRepository {

    override fun loadAd(
        onStateChanged: (AdState) -> Unit
    ) {
        appOpenAdManager.loadAd(onStateChanged)
    }

    override fun showAdIfAvailable(
        activity: Activity,
        forceShow: Boolean,
        onStateChanged: (AdState) -> Unit,
        onComplete: () -> Unit
    ) {
        appOpenAdManager.showAdIfAvailable(
            activity = activity,
            forceShow = forceShow,
            onStateChanged = onStateChanged,
            onComplete = onComplete
        )
    }

    override fun isAdReady(): Boolean {
        return appOpenAdManager.isAdReady()
    }

    override fun onAppMovedToBackground() {
        appOpenAdManager.onAppMovedToBackground()
    }

    override fun getCurrentConfig(): AppOpenAdConfig {
        return appOpenAdManager.getCurrentConfig()
    }
}