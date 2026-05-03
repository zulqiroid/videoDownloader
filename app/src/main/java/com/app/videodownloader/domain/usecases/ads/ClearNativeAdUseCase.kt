package com.app.videodownloader.domain.usecases.ads

import com.app.videodownloader.domain.repository.ads.NativeAdRepository

class ClearNativeAdUseCase(
    private val repository: NativeAdRepository
) {
    operator fun invoke(
        placementKey: String
    ) {
        repository.clearAd(placementKey)
    }
}