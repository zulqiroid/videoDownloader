package com.app.videodownloader.domain.repository

import com.app.videodownloader.domain.model.DownloadItem
import kotlinx.coroutines.flow.Flow

interface VideoDownloadRepository {

    suspend fun startDownload(url: String): Long

    suspend fun getAllDownloadedFiles(): List<DownloadItem>

    fun observeDownloads(): Flow<List<DownloadItem>>
    suspend fun cancelDownload(id: Long)
}