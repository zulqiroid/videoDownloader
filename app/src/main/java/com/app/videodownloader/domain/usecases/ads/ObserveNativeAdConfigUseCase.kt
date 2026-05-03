package com.app.videodownloader.domain.usecases.ads

import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.repository.RemoteConfigRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveNativeAdConfigUseCase(
    private val repository: RemoteConfigRepository
) {
    operator fun invoke(): StateFlow<NativeAdConfig> {
        return repository.nativeAdConfig
    }
}