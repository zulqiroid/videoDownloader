package com.app.videodownloader.domain.repository.ads

import com.app.videodownloader.domain.model.ads.AdState
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.flow.StateFlow

interface NativeAdRepository {

    val nativeAds: StateFlow<Map<String, NativeAd>>

    fun loadAd(
        placementKey: String,
        onStateChanged: (AdState) -> Unit = {}
    )

    fun clearAd(
        placementKey: String
    )

    fun clearAllAds()

    fun isAdReady(
        placementKey: String
    ): Boolean

    fun getCurrentConfig(): NativeAdConfig
}