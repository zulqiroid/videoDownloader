package com.app.videodownloader.presentation.screens.downloadGuide.events

sealed class DownloadGuideIntent {
    object OnBackClicked : DownloadGuideIntent()
    object OnGotItClicked : DownloadGuideIntent()
}