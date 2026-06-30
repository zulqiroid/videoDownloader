package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.MediaRepository

class GetVideosUseCase(
    private val repository: MediaRepository
) {
    suspend operator fun invoke() = repository.observeVideos()
}

class GetAudiosUseCase(
    private val repository: MediaRepository
) {
    suspend operator fun invoke() = repository.observeAudios()
}