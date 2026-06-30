package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads

import android.app.Activity
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads.AppOpenAdRepository

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