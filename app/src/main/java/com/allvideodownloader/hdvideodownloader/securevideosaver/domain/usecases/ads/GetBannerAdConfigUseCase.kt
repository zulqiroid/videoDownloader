package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.BannerAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.RemoteConfigRepository

class GetBannerAdConfigUseCase(
    private val repository: RemoteConfigRepository
) {
    operator fun invoke(): BannerAdConfig {
        return repository.getCurrentBannerAdConfig()
    }
}