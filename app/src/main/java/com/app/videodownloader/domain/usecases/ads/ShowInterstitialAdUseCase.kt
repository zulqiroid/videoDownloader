package com.app.videodownloader.domain.usecases.ads

import android.app.Activity
import com.app.videodownloader.domain.model.AdState
import com.app.videodownloader.domain.repository.ads.InterstitialAdRepository

/**
 * Displays the interstitial ad on the provided [activity].
 * Safe to call even if no ad is preloaded — fallback load is handled internally.
 */
class ShowInterstitialAdUseCase(
    private val repository: InterstitialAdRepository
) {
    operator fun invoke(activity: Activity, onStateChanged: (AdState) -> Unit) {
        repository.showAd(activity, onStateChanged)
    }
}