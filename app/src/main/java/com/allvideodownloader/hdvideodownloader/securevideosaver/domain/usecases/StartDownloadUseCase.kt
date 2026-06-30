package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.DownloadItem
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.VideoDownloadRepository
import kotlinx.coroutines.flow.Flow

class StartDownloadUseCase(
    private val repository: VideoDownloadRepository
) {
    suspend operator fun invoke(url: String): Long {
        return repository.startDownload(url)
    }
}


class ObserveDownloadsUseCase(
    private val repository: VideoDownloadRepository
) {
    operator fun invoke(): Flow<List<DownloadItem>> {
        return repository.observeDownloads()
    }
}


class PauseDownloadUseCase(
    private val repository: VideoDownloadRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.pauseDownload(id)
    }
}

class ResumeDownloadUseCase(
    private val repository: VideoDownloadRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.resumeDownload(id)
    }
}
