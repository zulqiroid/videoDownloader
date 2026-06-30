package com.allvideodownloader.hdvideodownloader.securevideosaver.data.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FullScreenAdCoordinator {

    private val _isFullScreenAdShowing = MutableStateFlow(false)
    val isFullScreenAdShowing: StateFlow<Boolean> = _isFullScreenAdShowing.asStateFlow()

    fun onFullScreenAdStarted() {
        _isFullScreenAdShowing.value = true
    }

    fun onFullScreenAdFinished() {
        _isFullScreenAdShowing.value = false
    }

    fun isAnyFullScreenAdShowing(): Boolean {
        return _isFullScreenAdShowing.value
    }
}