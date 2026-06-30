package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.DownloadItem
import kotlinx.coroutines.flow.Flow



interface VideoDownloadRepository {

    fun observeDownloads(): Flow<List<DownloadItem>>

    suspend fun startDownload(url: String): Long

    suspend fun pauseDownload(id: Long)

    suspend fun resumeDownload(id: Long)

    suspend fun cancelDownload(id: Long)

    suspend fun getAllDownloadedFiles(): List<DownloadItem>
}