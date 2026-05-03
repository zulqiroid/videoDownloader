package com.app.videodownloader.domain.usecases.ads

import com.app.videodownloader.domain.model.ads.AdState
import com.app.videodownloader.domain.repository.ads.NativeAdRepository

class LoadNativeAdUseCase(
    private val repository: NativeAdRepository
) {
    operator fun invoke(
        placementKey: String,
        onStateChanged: (AdState) -> Unit = {}
    ) {
        repository.loadAd(
            placementKey = placementKey,
            onStateChanged = onStateChanged
        )
    }
}