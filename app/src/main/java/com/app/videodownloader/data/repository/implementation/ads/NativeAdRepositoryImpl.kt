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

    override val nativeAdPools: StateFlow<Map<String, Map<String, NativeAd>>>
        get() = nativeAdManager.nativeAdPools

    override fun loadAd(
        placementKey: String,
        slotKey: String,
        onStateChanged: (AdState) -> Unit
    ) {
        nativeAdManager.loadAd(
            placementKey = placementKey,
            slotKey = slotKey,
            onStateChanged = onStateChanged
        )
    }

    override fun clearAd(
        placementKey: String,
        slotKey: String
    ) {
        nativeAdManager.clearAd(
            placementKey = placementKey,
            slotKey = slotKey
        )
    }

    override fun clearPlacement(
        placementKey: String
    ) {
        nativeAdManager.clearPlacement(placementKey)
    }

    override fun clearAllAds() {
        nativeAdManager.clearAllAds()
    }

    override fun isAdReady(
        placementKey: String,
        slotKey: String
    ): Boolean {
        return nativeAdManager.isAdReady(
            placementKey = placementKey,
            slotKey = slotKey
        )
    }

    override fun getCurrentConfig(): NativeAdConfig {
        return nativeAdManager.getCurrentConfig()
    }
}