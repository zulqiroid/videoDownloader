package com.app.videodownloader.domain.usecases

import com.app.videodownloader.domain.repository.VideoDownloadRepository

class CancelDownloadUseCase(
    private val repository: VideoDownloadRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.cancelDownload(id)
    }
}