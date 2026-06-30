package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads

import android.app.Activity
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AppOpenAdConfig

interface AppOpenAdRepository {

    fun loadAd(
        isSplash: Boolean,
        onStateChanged: (AdState) -> Unit = {}
    )

    fun showAdIfAvailable(
        activity: Activity,
        forceShow: Boolean = false,
        onStateChanged: (AdState) -> Unit = {},
        onComplete: () -> Unit = {}
    )

    fun isAdReady(): Boolean

    fun onAppMovedToBackground()

    fun getCurrentConfig(): AppOpenAdConfig
}