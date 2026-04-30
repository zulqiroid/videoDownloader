package com.app.videodownloader.domain.repository

import com.app.videodownloader.presentation.screens.player.states.PlayerUiItem

interface MediaRepository {
    suspend fun getVideos(): List<PlayerUiItem>
    suspend fun getAudios(): List<PlayerUiItem>
}