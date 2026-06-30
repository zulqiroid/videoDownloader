package com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation

import com.allvideodownloader.hdvideodownloader.securevideosaver.data.mapper.toDomain
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.remote.ReelsApi
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ReelCategory
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ReelRepository

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