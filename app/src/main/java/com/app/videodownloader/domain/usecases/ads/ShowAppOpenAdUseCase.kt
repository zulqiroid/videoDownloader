package com.app.videodownloader.domain.usecases.ads

import android.app.Activity
import com.app.videodownloader.domain.model.ads.AdState
import com.app.videodownloader.domain.repository.ads.AdRepository
import com.app.videodownloader.domain.repository.ads.AppOpenAdRepository

class ShowAppOpenAdUseCase(
    private val repository: AppOpenAdRepository
) {
    operator fun invoke(
        activity: Activity,
        forceShow: Boolean = false,
        onStateChanged: (AdState) -> Unit = {},
        onComplete: () -> Unit = {}
    ) {
        repository.showAdIfAvailable(
            activity = activity,
            forceShow = forceShow,
            onStateChanged = onStateChanged,
            onComplete = onComplete
        )
    }
}