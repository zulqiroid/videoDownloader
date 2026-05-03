package com.app.videodownloader.domain.usecases.ads

import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.repository.RemoteConfigRepository

class GetNativeAdConfigUseCase(
    private val repository: RemoteConfigRepository
) {
    operator fun invoke(): NativeAdConfig {
        return repository.getCurrentNativeAdConfig()
    }
}