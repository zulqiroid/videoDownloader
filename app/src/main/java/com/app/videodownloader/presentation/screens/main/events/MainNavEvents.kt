package com.app.videodownloader.presentation.screens.main.events

import android.app.PendingIntent
import android.net.Uri
import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.domain.model.SocialPlatform
import com.app.videodownloader.presentation.screens.home.events.HomeNavEvents

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

}