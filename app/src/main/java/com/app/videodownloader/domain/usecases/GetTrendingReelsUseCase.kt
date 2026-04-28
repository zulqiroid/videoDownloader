package com.app.videodownloader.domain.usecases

import com.app.videodownloader.domain.model.Reel
import com.app.videodownloader.domain.model.ReelCategory
import com.app.videodownloader.domain.repository.ReelRepository

class GetTrendingReelsUseCase(
    private val repository: ReelRepository
) {
    suspend operator fun invoke(): List<ReelCategory> {
        return repository.getTrendingReels()
    }
}