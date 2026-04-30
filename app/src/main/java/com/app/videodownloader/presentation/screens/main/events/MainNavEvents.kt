package com.app.videodownloader.presentation.screens.main.events

import com.app.videodownloader.domain.model.SocialPlatform
import com.app.videodownloader.presentation.screens.home.events.HomeNavEvents

sealed class MainNavEvents {
    object ExitApp: MainNavEvents()
    data object ShowAd : MainNavEvents()
 }