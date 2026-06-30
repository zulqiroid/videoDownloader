package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository


import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.states.PlayerUiItem
import kotlinx.coroutines.flow.Flow

interface MediaRepository {

    fun observeVideos(): Flow<List<PlayerUiItem>>

    fun observeAudios(): Flow<List<PlayerUiItem>>
}