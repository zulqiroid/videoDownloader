package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.RemoteConfigRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveNativeAdConfigUseCase(
    private val repository: RemoteConfigRepository
) {
    operator fun invoke(): StateFlow<NativeAdConfig> {
        return repository.nativeAdConfig
    }
}