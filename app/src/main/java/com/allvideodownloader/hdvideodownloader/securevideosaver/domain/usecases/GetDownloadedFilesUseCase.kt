package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.DownloadItem
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.VideoDownloadRepository

class GetDownloadedFilesUseCase(
    private val repository: VideoDownloadRepository
) {
    suspend operator fun invoke(): List<DownloadItem> {
        return repository.getAllDownloadedFiles()
    }
}
