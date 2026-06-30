package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.RemoteConfigRepository

class GetNativeAdConfigUseCase(
    private val repository: RemoteConfigRepository
) {
    operator fun invoke(): NativeAdConfig {
        return repository.getCurrentNativeAdConfig()
    }
}