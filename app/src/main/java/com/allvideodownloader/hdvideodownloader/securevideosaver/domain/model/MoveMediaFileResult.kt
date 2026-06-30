package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model

import android.app.PendingIntent
import android.net.Uri

sealed interface MoveMediaFileResult {

    data class Success(
        val movedFileName: String
    ) : MoveMediaFileResult

    data class RequiresDeletePermission(
        val uri: Uri,
        val pendingIntent: PendingIntent?
    ) : MoveMediaFileResult

    data class Failure(
        val message: String
    ) : MoveMediaFileResult
}