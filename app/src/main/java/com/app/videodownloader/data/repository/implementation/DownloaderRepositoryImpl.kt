package com.app.videodownloader.data.repository.implementation

import com.app.videodownloader.data.mapper.toDomain
import com.app.videodownloader.data.remote.DownloaderApi
import com.app.videodownloader.domain.model.VideoData
import com.app.videodownloader.domain.repository.DownloaderRepository

class DownloaderRepositoryImpl(
    private val api: DownloaderApi
) : DownloaderRepository {

    override suspend fun fetchVideo(url: String): VideoData {
        return try {
            api.download(url).toDomain()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}