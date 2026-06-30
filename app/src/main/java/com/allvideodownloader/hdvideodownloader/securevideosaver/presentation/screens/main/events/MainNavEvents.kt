package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.events

import android.app.PendingIntent
import android.net.Uri
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile

sealed class MainNavEvents {

    object ExitApp: MainNavEvents()
    data class RequestMediaWritePermission(
        val uri: Uri,
        val pendingIntent: PendingIntent?
    ) : MainNavEvents()

    data class RequestMediaDeletePermission(
        val uri: Uri,
        val pendingIntent: PendingIntent?
    ) : MainNavEvents()

    data class RequestMoveDeletePermission(
        val uri: Uri,
        val pendingIntent: PendingIntent?
    ) : MainNavEvents()

    object PickMoveDestinationFolder: MainNavEvents()

    data class ShareMediaFile(
        val mediaFile: MediaFile
    ) : MainNavEvents()

    data class PlayMediaFile(
        val mediaFile: MediaFile
    ) : MainNavEvents()

    data class OpenMediaPlayer(
        val mediaList: List<MediaFile>,
        val startIndex: Int
    ) : MainNavEvents()

    object NavigateToAppLanguageSRC : MainNavEvents()

    data object OpenAppStoreForRating : MainNavEvents()

    data class SendFeedbackEmail(
        val subject: String,
        val message: String
    ) : MainNavEvents()

    data object NavigateToPrivacyPolicy: MainNavEvents()

}