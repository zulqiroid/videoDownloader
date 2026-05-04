package com.app.videodownloader.domain.usecases.ads

import com.app.videodownloader.domain.model.ads.AdState
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.repository.ads.NativeAdRepository

class LoadNativeAdUseCase(
    private val repository: NativeAdRepository
) {
    operator fun invoke(
        placementKey: String,
        slotKey: String = NativeAdConfig.DEFAULT_SLOT,
        onStateChanged: (AdState) -> Unit = {}
    ) {
        repository.loadAd(
            placementKey = placementKey,
            slotKey = slotKey,
            onStateChanged = onStateChanged
        )
    }
}