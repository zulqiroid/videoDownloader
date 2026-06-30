package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.events

import android.app.PendingIntent
import android.net.Uri
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile

sealed interface MediaPlayerNavEvent {

    data class RequestMediaWritePermission(
        val uri: Uri,
        val pendingIntent: PendingIntent? = null
    ) : MediaPlayerNavEvent

    data class RequestMediaDeletePermission(
        val uri: Uri,
        val pendingIntent: PendingIntent? = null
    ) : MediaPlayerNavEvent

    data object RequestWriteSettingsPermission : MediaPlayerNavEvent

    data class ShareMediaFile(
        val mediaFile: MediaFile
    ) : MediaPlayerNavEvent

    data object CloseMediaPlayer : MediaPlayerNavEvent

    data object RequestPictureInPicture : MediaPlayerNavEvent
}