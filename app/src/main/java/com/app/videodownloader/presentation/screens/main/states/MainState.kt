package com.app.videodownloader.presentation.screens.main.states

import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.domain.model.Reel
import com.app.videodownloader.domain.model.VideoData

data class MainState(
    val selectedTab: BottomNavItem = BottomNavItem.Home,
    val isDrawerOpen: Boolean = false,
    val showPolicyDialogue: Boolean = false,
    val urlFetchingLoading: Boolean = false,

    val showExitDialogue : Boolean = false,

    val videoData: VideoData? = null,
    val error: String? = null,


    val showDownloadSheet: Boolean = false,
    val selectedOptionIndex: Int = 0,

    val showDownloadProgressDialogue: Boolean = false,

    val selectedReel : Reel? = null,

    val showPlayerDialogue: Boolean = false,
    val playerMediaItem: MediaFile? = null,
    )