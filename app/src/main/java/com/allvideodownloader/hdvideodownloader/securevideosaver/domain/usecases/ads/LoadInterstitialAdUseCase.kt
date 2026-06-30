package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdsScreens
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads.InterstitialAdRepository

class LoadInterstitialAdUseCase(
    private val repository: InterstitialAdRepository
) {
    operator fun invoke(
        AdScreen: AdsScreens,
        onStateChanged: (AdState) -> Unit = {}
    ) {
        repository.loadAd(AdScreen = AdScreen ,onStateChanged=  onStateChanged)
    }
}