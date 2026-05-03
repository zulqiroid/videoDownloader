package com.app.videodownloader.domain.usecases.ads

import com.app.videodownloader.domain.model.ads.AppOpenAdConfig
import com.app.videodownloader.domain.repository.RemoteConfigRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveAppOpenAdConfigUseCase(
    private val repository: RemoteConfigRepository
) {
    operator fun invoke(): StateFlow<AppOpenAdConfig> {
        return repository.appOpenAdConfig
    }
}