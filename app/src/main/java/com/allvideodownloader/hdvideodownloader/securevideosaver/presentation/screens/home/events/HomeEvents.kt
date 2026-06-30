package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.home.events

sealed class HomeEvents {
    object LoadReels : HomeEvents()
    object Refresh : HomeEvents()
    data class OnUrlChange(val value: String) : HomeEvents()
 }