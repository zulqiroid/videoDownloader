package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads.NativeAdRepository

class ClearAllNativeAdsUseCase(
    private val repository: NativeAdRepository
) {
    operator fun invoke() {
        repository.clearAllAds()
    }
}