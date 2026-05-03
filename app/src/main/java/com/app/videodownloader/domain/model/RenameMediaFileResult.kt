package com.app.videodownloader.domain.model

import android.app.PendingIntent
import android.net.Uri

sealed interface RenameMediaFileResult {

    data class Success(
        val mediaFile: MediaFile
    ) : RenameMediaFileResult

    data class RequiresWritePermission(
        val uri: Uri,
        val pendingIntent: PendingIntent? = null
    ) : RenameMediaFileResult

    data class Failure(
        val message: String
    ) : RenameMediaFileResult
}