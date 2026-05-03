package com.app.videodownloader.domain.usecases.ads

import com.app.videodownloader.domain.model.ads.AdState
import com.app.videodownloader.domain.repository.ads.AdRepository
import com.app.videodownloader.domain.repository.ads.AppOpenAdRepository

class LoadAppOpenAdUseCase(
    private val repository: AppOpenAdRepository
) {
    operator fun invoke(
        onStateChanged: (AdState) -> Unit = {}
    ) {
        repository.loadAd(onStateChanged)
    }
}