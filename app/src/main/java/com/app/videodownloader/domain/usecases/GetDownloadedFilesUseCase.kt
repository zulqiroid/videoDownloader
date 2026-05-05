package com.app.videodownloader.domain.usecases

import com.app.videodownloader.domain.model.DownloadItem
import com.app.videodownloader.domain.repository.VideoDownloadRepository

class GetDownloadedFilesUseCase(
    private val repository: VideoDownloadRepository
) {
    suspend operator fun invoke(): List<DownloadItem> {
        return repository.getAllDownloadedFiles()
    }
}
