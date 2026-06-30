package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.downloadGuide.events

sealed class DownloadGuideIntent {
    object OnBackClicked : DownloadGuideIntent()
    object OnGotItClicked : DownloadGuideIntent()
}