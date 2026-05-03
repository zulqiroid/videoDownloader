package com.app.videodownloader.domain.repository.ads

import android.app.Activity
import com.app.videodownloader.domain.model.ads.AdState
import com.app.videodownloader.domain.model.ads.AppOpenAdConfig

interface AppOpenAdRepository {

    fun loadAd(
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