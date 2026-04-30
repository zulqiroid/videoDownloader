package com.app.videodownloader.domain.usecases

 import com.app.videodownloader.data.local.dataSource.DownloadTracker
import com.app.videodownloader.domain.repository.VideoDownloadRepository
import kotlinx.coroutines.flow.Flow
/*

class DownloadVideoUseCase(
    private val repository: VideoDownloadRepository,
    private val tracker: DownloadTracker
) {

    suspend operator fun invoke(url: String): Flow<DownloadProgress> {
        val id = repository.startDownload(url)
        return tracker.track(id)
    }
}*/
