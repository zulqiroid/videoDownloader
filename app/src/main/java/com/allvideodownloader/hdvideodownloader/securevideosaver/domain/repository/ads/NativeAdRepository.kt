package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.flow.StateFlow

interface NativeAdRepository {

    /**
     * Backward-compatible single-ad map.
     * Used by screens that show only one ad per placement.
     */
    val nativeAds: StateFlow<Map<String, NativeAd>>

    /**
     * Professional pool:
     * placementKey -> slotKey -> NativeAd
     */
    val nativeAdPools: StateFlow<Map<String, Map<String, NativeAd>>>

    fun loadAd(
        placementKey: String,
        slotKey: String,
        onStateChanged: (AdState) -> Unit = {}
    )

    fun clearAd(
        placementKey: String,
        slotKey: String
    )

    fun clearPlacement(
        placementKey: String
    )

    fun clearAllAds()

    fun isAdReady(
        placementKey: String,
        slotKey: String
    ): Boolean

    fun getCurrentConfig(): NativeAdConfig
}