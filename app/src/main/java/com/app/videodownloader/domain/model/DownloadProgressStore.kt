package com.app.videodownloader.domain.model

import com.app.videodownloader.domain.model.DownloadItem
import kotlinx.coroutines.flow.Flow

interface DownloadProgressStore {

    fun observeDownloads(): Flow<List<DownloadItem>>

    suspend fun upsert(item: DownloadItem)

    suspend fun remove(id: Long)

    suspend fun get(id: Long): DownloadItem?

    suspend fun getAll(): List<DownloadItem>

    suspend fun clear()
}