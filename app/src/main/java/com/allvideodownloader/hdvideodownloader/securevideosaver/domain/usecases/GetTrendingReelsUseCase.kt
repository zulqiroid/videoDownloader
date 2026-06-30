package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ReelCategory
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ReelRepository

class GetTrendingReelsUseCase(
    private val repository: ReelRepository
) {
    suspend operator fun invoke(): List<ReelCategory> {
        return repository.getTrendingReels()
    }
}