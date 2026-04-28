package com.app.videodownloader.domain.repository

interface VideoDownloadRepository {
    suspend fun downloadVideo(url: String)
}