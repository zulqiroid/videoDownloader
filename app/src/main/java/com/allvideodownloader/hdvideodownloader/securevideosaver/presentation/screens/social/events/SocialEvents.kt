package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.social.events

sealed class SocialEvents {
    object LoadReels : SocialEvents()
    object Refresh : SocialEvents()
    data class OnUrlChange(val value: String) : SocialEvents()
 }