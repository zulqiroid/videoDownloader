package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.InterstitialAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.RemoteConfigRepository

class GetInterstitialAdConfigUseCase(
    private val repository: RemoteConfigRepository
) {
    operator fun invoke(): InterstitialAdConfig {
        return repository.getCurrentInterstitialAdConfig()
    }
}