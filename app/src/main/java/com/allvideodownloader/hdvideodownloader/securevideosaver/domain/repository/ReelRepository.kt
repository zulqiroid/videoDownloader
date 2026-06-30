package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ReelCategory

interface ReelRepository {
    suspend fun getTrendingReels(): List<ReelCategory>
}