package com.app.videodownloader.presentation.screens.social.events

import com.app.videodownloader.domain.model.Reel
import com.app.videodownloader.presentation.screens.home.events.HomeEvents

sealed class SocialEvents {
    object LoadReels : SocialEvents()
    object Refresh : SocialEvents()
    data class OnUrlChange(val value: String) : SocialEvents()
 }