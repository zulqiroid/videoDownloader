package com.app.videodownloader.data.repository.implementation.ads

import com.app.videodownloader.data.manager.NativeAdManager
import com.app.videodownloader.domain.model.ads.AdState
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.repository.ads.NativeAdRepository
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.flow.StateFlow

class NativeAdRepositoryImpl(
    private val nativeAdManager: NativeAdManager
) : NativeAdRepository {

    override val nativeAds: StateFlow<Map<String, NativeAd>>
        get() = nativeAdManager.nativeAds

    override fun loadAd(
        placementKey: String,
        onStateChanged: (AdState) -> Unit
    ) {
        nativeAdManager.loadAd(
            placementKey = placementKey,
            onStateChanged = onStateChanged
        )
    }

    override fun clearAd(
        placementKey: String
    ) {
        nativeAdManager.clearAd(placementKey)
    }

    override fun clearAllAds() {
        nativeAdManager.clearAllAds()
    }

    override fun isAdReady(
        placementKey: String
    ): Boolean {
        return nativeAdManager.isAdReady(placementKey)
    }

    override fun getCurrentConfig(): NativeAdConfig {
        return nativeAdManager.getCurrentConfig()
    }
}