package com.app.videodownloader.domain.usecases.ads

import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.repository.ads.NativeAdRepository

class ClearNativeAdUseCase(
    private val repository: NativeAdRepository
) {
    operator fun invoke(
        placementKey: String,
        slotKey: String = NativeAdConfig.DEFAULT_SLOT
    ) {
        repository.clearAd(
            placementKey = placementKey,
            slotKey = slotKey
        )
    }
}