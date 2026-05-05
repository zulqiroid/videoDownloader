package com.app.videodownloader.domain.model.appUpdate

sealed interface AppUpdateResult {
    data object NoUpdateRequired : AppUpdateResult
    data object UpdateStarted : AppUpdateResult
    data object UpdateNotAvailable : AppUpdateResult
    data class Failed(val message: String) : AppUpdateResult
}