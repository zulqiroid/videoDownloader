package com.app.videodownloader.presentation.screens.download.events


sealed class DownloadEvents {

    data class OnPauseDownloadingClicked(
        val id: Long
    ) : DownloadEvents()

    data class OnResumeDownloadingClicked(
        val id: Long
    ) : DownloadEvents()

    data class OnDeleteDownloadingClicked(
        val id: Long
    ) : DownloadEvents()
}