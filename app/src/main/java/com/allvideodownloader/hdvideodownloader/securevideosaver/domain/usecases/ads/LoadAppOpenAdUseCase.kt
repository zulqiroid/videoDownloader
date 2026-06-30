package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads.AppOpenAdRepository

class LoadAppOpenAdUseCase(
    private val repository: AppOpenAdRepository
) {
    operator fun invoke(
        isSplash: Boolean,
        onStateChanged: (AdState) -> Unit = {}
    ) {
        repository.loadAd(isSplash = isSplash, onStateChanged)
    }
}