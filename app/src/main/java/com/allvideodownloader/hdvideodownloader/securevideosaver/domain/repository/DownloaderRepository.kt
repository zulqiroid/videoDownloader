package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.VideoData

interface DownloaderRepository {
    suspend fun fetchVideo(url: String): VideoData
}