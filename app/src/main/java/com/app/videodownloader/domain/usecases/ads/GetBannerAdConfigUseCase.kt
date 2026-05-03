package com.app.videodownloader.domain.usecases.ads

import com.app.videodownloader.domain.model.ads.BannerAdConfig
import com.app.videodownloader.domain.repository.RemoteConfigRepository

class GetBannerAdConfigUseCase(
    private val repository: RemoteConfigRepository
) {
    operator fun invoke(): BannerAdConfig {
        return repository.getCurrentBannerAdConfig()
    }
}