package com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.ads

import android.app.Activity
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.manager.InterstitialAdManager
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdsScreens
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.InterstitialAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.InterstitialAdPlacement
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads.InterstitialAdRepository

class InterstitialAdRepositoryImpl(
    private val interstitialAdManager: InterstitialAdManager
) : InterstitialAdRepository {

    override fun loadAd(
        AdScreen: AdsScreens,
        onStateChanged: (AdState) -> Unit
    ) {
        interstitialAdManager.loadAd(AdScreen = AdScreen, onStateChanged =  onStateChanged)
    }

    override fun showAdIfAvailable(
        activity: Activity,
        placement: InterstitialAdPlacement,
        forceShow: Boolean,
        onStateChanged: (AdState) -> Unit,
        onComplete: () -> Unit
    ) {
        interstitialAdManager.showAdIfAvailable(
            activity = activity,
            placement = placement,
            forceShow = forceShow,
            onStateChanged = onStateChanged,
            onComplete = onComplete
        )
    }

    override fun isAdReady(): Boolean {
        return interstitialAdManager.isAdReady()
    }

    override fun getCurrentConfig(): InterstitialAdConfig {
        return interstitialAdManager.getCurrentConfig()
    }
}