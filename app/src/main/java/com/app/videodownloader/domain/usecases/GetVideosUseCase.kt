package com.app.videodownloader.domain.usecases

import com.app.videodownloader.domain.repository.MediaRepository

class GetVideosUseCase(
    private val repository: MediaRepository
) {
    suspend operator fun invoke() = repository.getVideos()
}

class GetAudiosUseCase(
    private val repository: MediaRepository
) {
    suspend operator fun invoke() = repository.getAudios()
}