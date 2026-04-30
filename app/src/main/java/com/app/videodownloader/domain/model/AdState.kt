package com.app.videodownloader.domain.model

/**
 * Represents the lifecycle state of an Ad.
 * Used to communicate ad events across layers via callbacks/flows.
 */
sealed class AdState {
    object Idle : AdState()
    object Loading : AdState()
    object Loaded : AdState()
    object Showing : AdState()
    object Dismissed : AdState()
    data class LoadFailed(val errorCode: Int, val errorMessage: String) : AdState()
    data class ShowFailed(val errorMessage: String) : AdState()
}