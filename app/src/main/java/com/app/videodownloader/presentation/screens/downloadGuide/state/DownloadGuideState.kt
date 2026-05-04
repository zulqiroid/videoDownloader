package com.app.videodownloader.presentation.screens.downloadGuide.state


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class DownloadGuideState(
    val steps: List<GuideStep> = emptyList()
)




data class GuideStep(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val icon: Int
)