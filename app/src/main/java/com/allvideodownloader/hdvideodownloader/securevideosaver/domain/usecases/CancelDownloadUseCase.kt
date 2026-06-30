package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.VideoDownloadRepository


class CancelDownloadUseCase(
    private val repository: VideoDownloadRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.cancelDownload(id)
    }
}