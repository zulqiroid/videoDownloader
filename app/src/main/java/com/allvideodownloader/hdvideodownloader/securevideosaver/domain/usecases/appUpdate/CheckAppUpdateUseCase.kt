package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.appUpdate

import android.app.Activity
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.appUpdate.AppUpdateRepository

class CheckAppUpdateUseCase(
    private val appUpdateRepository: AppUpdateRepository,
) {
    suspend operator fun invoke(
        activity: Activity,
    ) = appUpdateRepository.checkAndStartImmediateUpdateIfRequired(activity)
}