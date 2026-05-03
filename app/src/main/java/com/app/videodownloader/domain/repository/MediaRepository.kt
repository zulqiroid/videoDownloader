package com.app.videodownloader.domain.repository


import com.app.videodownloader.presentation.screens.player.states.PlayerUiItem
import kotlinx.coroutines.flow.Flow

interface MediaRepository {

    fun observeVideos(): Flow<List<PlayerUiItem>>

    fun observeAudios(): Flow<List<PlayerUiItem>>
}