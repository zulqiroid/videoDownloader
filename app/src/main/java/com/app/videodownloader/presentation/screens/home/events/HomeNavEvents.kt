package com.app.videodownloader.presentation.screens.home.events

import com.app.videodownloader.domain.model.SocialPlatform

sealed class HomeNavEvents {
    data class NavigateToPlayer(val reelId: String) : HomeNavEvents()
}