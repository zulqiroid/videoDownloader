package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads

import android.app.Activity
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdsScreens
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.InterstitialAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.InterstitialAdPlacement

interface InterstitialAdRepository {

    fun loadAd(
        AdScreen: AdsScreens,
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