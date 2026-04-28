package com.app.videodownloader.data.repository.implementation

import com.app.videodownloader.data.mapper.toDomain
import com.app.videodownloader.data.remote.ReelsApi
import com.app.videodownloader.domain.model.Reel
import com.app.videodownloader.domain.model.ReelCategory
import com.app.videodownloader.domain.repository.ReelRepository

class ReelRepoImpl(
    private val api: ReelsApi
): ReelRepository {
    override suspend fun getTrendingReels(): List<ReelCategory> {
        return try {
            api.getReels().toDomain()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}