package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.home.events

sealed class HomeNavEvents {
    data class NavigateToPlayer(val reelId: String) : HomeNavEvents()
}