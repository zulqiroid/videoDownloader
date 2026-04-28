package com.app.videodownloader.presentation.screens.home.events

sealed class HomeEvents {
    object LoadReels : HomeEvents()
    object Refresh : HomeEvents()
    data class OnUrlChange(val value: String) : HomeEvents()
 }