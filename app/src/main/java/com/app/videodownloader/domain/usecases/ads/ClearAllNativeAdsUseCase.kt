package com.app.videodownloader.domain.usecases.ads

import com.app.videodownloader.domain.repository.ads.NativeAdRepository

class ClearAllNativeAdsUseCase(
    private val repository: NativeAdRepository
) {
    operator fun invoke() {
        repository.clearAllAds()
    }
}