package com.app.videodownloader.presentation.screens.home.events

sealed class HomeNavEvents {
         data class NavigateToPlayer(val reelId: String) : HomeNavEvents()
    }