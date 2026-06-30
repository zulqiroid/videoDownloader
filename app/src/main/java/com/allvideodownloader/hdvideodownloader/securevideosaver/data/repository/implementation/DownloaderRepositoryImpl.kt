package com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation

import com.allvideodownloader.hdvideodownloader.securevideosaver.data.mapper.toDomain
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.remote.DownloaderApi
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.VideoData
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.DownloaderRepository

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