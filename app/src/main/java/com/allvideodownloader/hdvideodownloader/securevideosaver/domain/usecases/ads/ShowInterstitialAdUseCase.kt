package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads

import android.app.Activity
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.InterstitialAdPlacement
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads.InterstitialAdRepository

class ShowInterstitialAdUseCase(
    private val repository: InterstitialAdRepository
) {
    operator fun invoke(
        activity: Activity,
        placement: InterstitialAdPlacement = InterstitialAdPlacement.Generic,
        forceShow: Boolean = false,
        onStateChanged: (AdState) -> Unit = {},
        onComplete: () -> Unit = {}
    ) {
        repository.showAdIfAvailable(
            activity = activity,
            placement = placement,
            forceShow = forceShow,
            onStateChanged = onStateChanged,
            onComplete = onComplete
        )
    }
}