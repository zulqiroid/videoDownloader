package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AppOpenAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.RemoteConfigRepository

class GetAppOpenAdConfigUseCase(
    private val repository: RemoteConfigRepository
) {
    operator fun invoke(): AppOpenAdConfig {
        return repository.getCurrentAppOpenAdConfig()
    }
}