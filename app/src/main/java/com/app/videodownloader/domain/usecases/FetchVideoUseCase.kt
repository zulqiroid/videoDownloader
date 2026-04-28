package com.app.videodownloader.domain.usecases

import com.app.videodownloader.domain.model.VideoData
import com.app.videodownloader.domain.repository.DownloaderRepository

class FetchVideoUseCase(
    private val repository: DownloaderRepository
) {

    suspend operator fun invoke(url: String): VideoData {
        if (url.isBlank()) throw IllegalArgumentException("URL cannot be empty")
        return repository.fetchVideo(url)
    }
}