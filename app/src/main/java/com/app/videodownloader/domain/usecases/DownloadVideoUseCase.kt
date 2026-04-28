package com.app.videodownloader.domain.usecases

import com.app.videodownloader.domain.repository.VideoDownloadRepository

class DownloadVideoUseCase(
    private val repository: VideoDownloadRepository
) {
    suspend operator fun invoke(url: String) {
        repository.downloadVideo(url)
    }
}