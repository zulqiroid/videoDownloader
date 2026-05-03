package com.app.videodownloader.data.repository.implementation.ads

import android.app.Activity
import com.app.videodownloader.data.manager.InterstitialAdManager
import com.app.videodownloader.domain.model.ads.AdState
import com.app.videodownloader.domain.model.ads.InterstitialAdConfig
import com.app.videodownloader.domain.model.ads.InterstitialAdPlacement
import com.app.videodownloader.domain.repository.ads.InterstitialAdRepository

class InterstitialAdRepositoryImpl(
    private val interstitialAdManager: InterstitialAdManager
) : InterstitialAdRepository {

    override fun loadAd(
        onStateChanged: (AdState) -> Unit
    ) {
        interstitialAdManager.loadAd(onStateChanged)
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