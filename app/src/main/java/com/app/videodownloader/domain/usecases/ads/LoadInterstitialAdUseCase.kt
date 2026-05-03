package com.app.videodownloader.domain.usecases.ads

import com.app.videodownloader.domain.model.ads.AdState
import com.app.videodownloader.domain.repository.ads.InterstitialAdRepository

class LoadInterstitialAdUseCase(
    private val repository: InterstitialAdRepository
) {
    operator fun invoke(
        onStateChanged: (AdState) -> Unit = {}
    ) {
        repository.loadAd(onStateChanged)
    }
}