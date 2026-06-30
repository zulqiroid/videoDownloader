package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.appUpdate

import android.app.Activity
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.appUpdate.AppUpdateResult

interface AppUpdateRepository {

    suspend fun checkAndStartImmediateUpdateIfRequired(
        activity: Activity,
    ): AppUpdateResult

    fun completeFlexibleUpdateIfDownloaded()
}