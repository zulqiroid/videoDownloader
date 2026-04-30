package com.app.videodownloader.domain.usecases.ads

import android.app.Activity
import com.app.videodownloader.domain.model.AdState
import com.app.videodownloader.domain.repository.ads.AdRepository

/**
 * Use case responsible for displaying the App Open Ad.
 *
 * Encapsulates all show-time business rules:
 *  - Delegates readiness checks to the repository.
 *  - Hands off activity context safely; never holds a reference beyond the call.
 */
class ShowAppOpenAdUseCase(
    private val adRepository: AdRepository
) {
    /**
     * @param activity       Host activity; must be in a resumed state.
     * @param onStateChanged Receives each [AdState] transition during the show lifecycle.
     */
    operator fun invoke(activity: Activity, onStateChanged: (AdState) -> Unit) {
        adRepository.showAd(activity, onStateChanged)
    }
}