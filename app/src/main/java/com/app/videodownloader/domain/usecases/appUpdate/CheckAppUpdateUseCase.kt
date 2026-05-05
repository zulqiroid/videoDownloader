package com.app.videodownloader.domain.usecases.appUpdate

import android.app.Activity
import com.app.videodownloader.domain.repository.appUpdate.AppUpdateRepository

class CheckAppUpdateUseCase(
    private val appUpdateRepository: AppUpdateRepository,
) {
    suspend operator fun invoke(
        activity: Activity,
    ) = appUpdateRepository.checkAndStartImmediateUpdateIfRequired(activity)
}