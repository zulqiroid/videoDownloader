package com.app.videodownloader.data.repository.implementation

import com.app.videodownloader.domain.model.DownloadItem
import com.app.videodownloader.domain.model.DownloadProgressStore
 import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryDownloadProgressStore : DownloadProgressStore {

    private val mutex = Mutex()
    private val downloads = MutableStateFlow<List<DownloadItem>>(emptyList())

    override fun observeDownloads(): Flow<List<DownloadItem>> {
        return downloads
    }

    override suspend fun upsert(item: DownloadItem) {
        mutex.withLock {
            downloads.update { current ->
                (current.filterNot { it.id == item.id } + item)
                    .sortedByDescending { it.id }
            }
        }
    }

    override suspend fun remove(id: Long) {
        mutex.withLock {
            downloads.update { current ->
                current.filterNot { it.id == id }
            }
        }
    }

    override suspend fun get(id: Long): DownloadItem? {
        return downloads.value.firstOrNull { it.id == id }
    }

    override suspend fun getAll(): List<DownloadItem> {
        return downloads.value
    }

    override suspend fun clear() {
        mutex.withLock {
            downloads.value = emptyList()
        }
    }
}