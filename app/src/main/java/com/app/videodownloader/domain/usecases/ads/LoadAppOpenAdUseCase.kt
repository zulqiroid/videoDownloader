package com.app.videodownloader.domain.usecases.ads

import com.app.videodownloader.domain.model.AdState
import com.app.videodownloader.domain.repository.ads.AdRepository

/**
 * Use case responsible for triggering the ad load lifecycle.
 *
 * Single-responsibility: delegates entirely to [AdRepository.loadAd].
 * The ViewModel/Presenter should invoke this on app foreground events.
 */
class LoadAppOpenAdUseCase (
    private val adRepository: AdRepository
) {
    /**
     * @param onStateChanged Receives each [AdState] transition during the load lifecycle.
     */
    operator fun invoke(onStateChanged: (AdState) -> Unit) {
        adRepository.loadAd(onStateChanged)
    }
}