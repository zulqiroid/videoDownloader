package com.app.videodownloader.domain.repository

import com.app.videodownloader.domain.model.VideoData

interface DownloaderRepository {
    suspend fun fetchVideo(url: String): VideoData
}