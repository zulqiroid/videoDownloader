package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads.NativeAdRepository

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