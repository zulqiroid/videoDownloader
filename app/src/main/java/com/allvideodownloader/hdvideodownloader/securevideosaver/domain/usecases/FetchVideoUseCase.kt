package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.VideoData
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.DownloaderRepository

class FetchVideoUseCase(
    private val repository: DownloaderRepository
) {

    suspend operator fun invoke(url: String): VideoData {
        if (url.isBlank()) throw IllegalArgumentException("URL cannot be empty")
        return repository.fetchVideo(url)
    }
}