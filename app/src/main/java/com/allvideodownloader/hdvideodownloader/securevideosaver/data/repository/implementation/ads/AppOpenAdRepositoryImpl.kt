package com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.ads

import android.app.Activity
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.manager.AppOpenAdManager
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AppOpenAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads.AppOpenAdRepository

class AppOpenAdRepositoryImpl(
    private val appOpenAdManager: AppOpenAdManager
) : AppOpenAdRepository {

    override fun loadAd(
        isSplash: Boolean,
        onStateChanged: (AdState) -> Unit
    ) {
        appOpenAdManager.loadAd(isSplash = isSplash,onStateChanged)
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