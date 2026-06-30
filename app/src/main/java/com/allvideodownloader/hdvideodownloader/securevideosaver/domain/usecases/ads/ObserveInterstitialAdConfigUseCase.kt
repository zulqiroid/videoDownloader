package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.InterstitialAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.RemoteConfigRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveInterstitialAdConfigUseCase(
    private val repository: RemoteConfigRepository
) {
    operator fun invoke(): StateFlow<InterstitialAdConfig> {
        return repository.interstitialAdConfig
    }
}