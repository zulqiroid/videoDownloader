package com.app.videodownloader.domain.usecases.ads

import com.app.videodownloader.domain.model.ads.InterstitialAdConfig
import com.app.videodownloader.domain.repository.RemoteConfigRepository

class GetInterstitialAdConfigUseCase(
    private val repository: RemoteConfigRepository
) {
    operator fun invoke(): InterstitialAdConfig {
        return repository.getCurrentInterstitialAdConfig()
    }
}