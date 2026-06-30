package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model

import android.app.PendingIntent
import android.net.Uri

sealed interface DeleteMediaFileResult {

    data object Success : DeleteMediaFileResult

    data class RequiresDeletePermission(
        val uri: Uri,
        val pendingIntent: PendingIntent? = null
    ) : DeleteMediaFileResult

    data class Failure(
        val message: String
    ) : DeleteMediaFileResult
}