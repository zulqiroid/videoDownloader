package com.app.videodownloader.presentation.screens.main.states

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
)