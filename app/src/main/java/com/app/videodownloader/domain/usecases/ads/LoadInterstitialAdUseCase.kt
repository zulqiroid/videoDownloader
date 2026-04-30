package com.app.videodownloader.domain.usecases.ads

import com.app.videodownloader.domain.model.AdState
 import com.app.videodownloader.domain.repository.ads.InterstitialAdRepository

/**
 * Triggers the interstitial ad load lifecycle.
 * Call this proactively — before the moment you need to show the ad.
 */
class LoadInterstitialAdUseCase(
    private val repository: InterstitialAdRepository
) {
    operator fun invoke(onStateChanged: (AdState) -> Unit) {
        repository.loadAd(onStateChanged)
    }
}