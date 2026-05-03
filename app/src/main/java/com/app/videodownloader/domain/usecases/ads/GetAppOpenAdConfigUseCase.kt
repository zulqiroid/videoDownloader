package com.app.videodownloader.domain.usecases.ads

import com.app.videodownloader.domain.model.ads.AppOpenAdConfig
import com.app.videodownloader.domain.repository.RemoteConfigRepository

class GetAppOpenAdConfigUseCase(
    private val repository: RemoteConfigRepository
) {
    operator fun invoke(): AppOpenAdConfig {
        return repository.getCurrentAppOpenAdConfig()
    }
}