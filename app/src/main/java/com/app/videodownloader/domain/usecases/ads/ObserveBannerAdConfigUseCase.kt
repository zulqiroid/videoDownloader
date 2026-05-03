package com.app.videodownloader.domain.usecases.ads

import com.app.videodownloader.domain.model.ads.BannerAdConfig
import com.app.videodownloader.domain.repository.RemoteConfigRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveBannerAdConfigUseCase(
    private val repository: RemoteConfigRepository
) {
    operator fun invoke(): StateFlow<BannerAdConfig> {
        return repository.bannerAdConfig
    }
}