package com.app.videodownloader.data.repository.implementation.ads

import android.app.Activity
import com.app.videodownloader.data.manager.AppOpenAdManager
import com.app.videodownloader.domain.model.AdState
import com.app.videodownloader.domain.repository.ads.AdRepository

/**
 * Concrete implementation of [com.app.videodownloader.domain.repository.ads.AdRepository].
 *
 * Acts as the bridge between the domain layer's abstract contract
 * and the infrastructure-level [com.app.videodownloader.data.manager.AppOpenAdManager].
 *
 * This layer exists to:
 *  - Keep the domain layer free of SDK/platform knowledge.
 *  - Allow swapping or mocking [com.app.videodownloader.data.manager.AppOpenAdManager] in tests without
 *    changing any domain or presentation code.
 */
 class AdRepositoryImpl(
    private val adManager: AppOpenAdManager
) : AdRepository {

    override fun loadAd(onStateChanged: (AdState) -> Unit) {
        adManager.loadAd(onStateChanged)
    }

    override fun showAd(activity: Activity, onStateChanged: (AdState) -> Unit) {
        adManager.showAd(activity, onStateChanged)
    }

    override fun isAdReady(): Boolean =
        adManager.isAdReady()
}