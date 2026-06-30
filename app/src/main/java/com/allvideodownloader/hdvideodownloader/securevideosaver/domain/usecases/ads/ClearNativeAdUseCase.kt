package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads.NativeAdRepository

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