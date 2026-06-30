package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads

sealed interface AdState {
    data object Idle : AdState
    data object Loading : AdState
    data object Loaded : AdState
    data object Showing : AdState
    data object Dismissed : AdState
    data object Clicked : AdState
    data object Impression : AdState

    data class Skipped(
        val reason: String
    ) : AdState

    data class LoadFailed(
        val errorCode: Int,
        val errorMessage: String
    ) : AdState

    data class ShowFailed(
        val errorMessage: String
    ) : AdState
}