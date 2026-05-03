package com.app.videodownloader.domain.usecases.ads

import com.app.videodownloader.domain.model.ads.InterstitialAdConfig
import com.app.videodownloader.domain.repository.RemoteConfigRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveInterstitialAdConfigUseCase(
    private val repository: RemoteConfigRepository
) {
    operator fun invoke(): StateFlow<InterstitialAdConfig> {
        return repository.interstitialAdConfig
    }
}