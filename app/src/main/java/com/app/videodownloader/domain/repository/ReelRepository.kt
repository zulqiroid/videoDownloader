package com.app.videodownloader.domain.repository

import com.app.videodownloader.domain.model.Reel
import com.app.videodownloader.domain.model.ReelCategory

interface ReelRepository {
    suspend fun getTrendingReels(): List<ReelCategory>
}