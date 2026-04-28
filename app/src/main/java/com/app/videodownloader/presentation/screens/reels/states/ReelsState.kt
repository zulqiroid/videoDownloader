package com.app.videodownloader.presentation.screens.reels.states

data class ReelsState(
    val reels: List<ReelUi> = emptyList(),
    val currentIndex: Int = 0,
    val isLoading: Boolean = false
)

data class ReelUi(
    val id: String,
    val videoUrl: String,
    val username: String,
    val caption: String
)