package com.app.videodownloader.domain.repository.appUpdate

import android.app.Activity
import com.app.videodownloader.domain.model.appUpdate.AppUpdateResult

interface AppUpdateRepository {

    suspend fun checkAndStartImmediateUpdateIfRequired(
        activity: Activity,
    ): AppUpdateResult

    fun completeFlexibleUpdateIfDownloaded()
}