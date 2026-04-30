package com.app.videodownloader.data.repository.implementation.ads

import android.app.Activity
import com.app.videodownloader.data.manager.InterstitialAdManager
import com.app.videodownloader.domain.model.AdState
 import com.app.videodownloader.domain.repository.ads.InterstitialAdRepository

/**
 * Bridges [InterstitialAdRepository] (domain) ↔ [InterstitialAdManager] (data/SDK).
 *
 * This indirection keeps the domain layer completely SDK-agnostic
 * and makes the manager trivially swappable in tests.
 */
class InterstitialAdRepositoryImpl(
    private val adManager: InterstitialAdManager
) : InterstitialAdRepository {

    override fun loadAd(onStateChanged: (AdState) -> Unit) {
        adManager.loadAd(onStateChanged)
    }

    override fun showAd(activity: Activity, onStateChanged: (AdState) -> Unit) {
        adManager.showAd(activity, onStateChanged)
    }

    override fun isAdReady(): Boolean =
        adManager.isAdReady()
}