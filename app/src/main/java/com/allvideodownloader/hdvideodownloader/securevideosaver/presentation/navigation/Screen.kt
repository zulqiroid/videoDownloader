package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.FromWhichSrc
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey {

    @Serializable
    object Splash : Screen()

    @Serializable
    data class AppLanguage(val src: FromWhichSrc) : Screen()

    @Serializable
    object OnBoarding : Screen()

    @Serializable
    object Main : Screen()

    @Serializable
    data class MediaPlayer(
        val mediaList: List<MediaFile>,
        val startIndex: Int,
    ) : Screen()

    @Serializable
    object DownloadGuide : Screen()

    @Serializable
    object Premium : Screen()
}