package com.app.videodownloader.domain.repository.ads

import android.app.Activity
import com.app.videodownloader.domain.model.ads.AdState
import com.app.videodownloader.domain.model.ads.InterstitialAdConfig
import com.app.videodownloader.domain.model.ads.InterstitialAdPlacement

interface InterstitialAdRepository {

    fun loadAd(
        onStateChanged: (AdState) -> Unit = {}
    )

    fun showAdIfAvailable(
        activity: Activity,
        placement: InterstitialAdPlacement = InterstitialAdPlacement.Generic,
        forceShow: Boolean = false,
        onStateChanged: (AdState) -> Unit = {},
        onComplete: () -> Unit = {}
    )

    fun isAdReady(): Boolean

    fun getCurrentConfig(): InterstitialAdConfig
}