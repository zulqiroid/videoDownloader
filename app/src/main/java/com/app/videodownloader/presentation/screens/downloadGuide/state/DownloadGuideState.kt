package com.app.videodownloader.presentation.screens.downloadGuide.state

data class DownloadGuideState(
    val steps: List<GuideStep> = emptyList()
)

data class GuideStep(
    val title: String,
    val description: String,
    val icon: Int
)