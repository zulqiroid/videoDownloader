package com.app.videodownloader.presentation.screens.main.states

import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.domain.model.Reel
import com.app.videodownloader.domain.model.VideoData

data class MainState(
    val selectedTab: BottomNavItem = BottomNavItem.Home,
    val isDrawerOpen: Boolean = false,
    val showPolicyDialogue: Boolean = false,

    val isPremiumUser: Boolean = false,

    val urlFetchingLoading: Boolean = false,

    val showFetchFailedDialog: Boolean = false,
    val failedFetchUrl: String = "",
    val fetchErrorMessage: String? = null,

    val showExitDialogue : Boolean = false,

    val videoData: VideoData? = null,
    val error: String? = null,

    val showDownloadSheet: Boolean = false,
    val selectedOptionIndex: Int = 0,

    val showDownloadProgressDialogue: Boolean = false,

    val selectedReel : Reel? = null,

    val showPlayerDialogue: Boolean = false,
    val playerMediaItem: MediaFile? = null,

    val showNotificationDialog: Boolean = false,
    val downloadCompleteNotificationEnabled: Boolean = true,
    val downloadFailedNotificationEnabled: Boolean = true,
    val appUpdatesNotificationEnabled: Boolean = false,

    val draftDownloadCompleteNotificationEnabled: Boolean = true,
    val draftDownloadFailedNotificationEnabled: Boolean = true,
    val draftAppUpdatesNotificationEnabled: Boolean = false,

    val showFileInfoDialog: Boolean = false,
    val fileInfoMediaItem: MediaFile? = null,

    val showRenameFileDialog: Boolean = false,
    val renameMediaItem: MediaFile? = null,
    val renameDraftName: String = "",
    val renameError: String? = null,
    val isRenamingFile: Boolean = false,

    val showDeleteFileDialog: Boolean = false,
    val deleteMediaItem: MediaFile? = null,
    val isDeletingFile: Boolean = false,
    val deleteFileError: String? = null,

    val moveMediaItem: MediaFile? = null,
    val isMovingFile: Boolean = false,
    val moveFileError: String? = null,

    val showFeedbackDialog: Boolean = false,
    val feedbackMessage: String = "",
    val feedbackError: String? = null,
    val isSubmittingFeedback: Boolean = false,

    val showRateUsDialog: Boolean = false,
    val selectedRating: Int = 4,
    val rateUsError: String? = null,
    val isSubmittingRating: Boolean = false,

    val isMediaPermissionGranted: Boolean = false,
    val showMediaPermissionDialog: Boolean = false,
    val shouldOpenMediaPermissionSettings: Boolean = false,
    val isNotificationPermissionGranted: Boolean = false,

    val isPlayerSearchActive: Boolean = false,
    val playerSearchQuery: String = "",

    val isPremiumIconVisible: Boolean = true,

    val isDownloadSearchActive: Boolean = false,
    val downloadSearchQuery: String = "",
)