package com.app.videodownloader.domain.usecases.ads

import com.app.videodownloader.domain.repository.ads.NativeAdRepository
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.flow.StateFlow

class ObserveNativeAdsUseCase(
    private val repository: NativeAdRepository
) {
    operator fun invoke(): StateFlow<Map<String, NativeAd>> {
        return repository.nativeAds
    }
}