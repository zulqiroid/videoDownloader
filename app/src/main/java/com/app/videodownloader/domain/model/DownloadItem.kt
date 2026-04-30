package com.app.videodownloader.domain.model
data class DownloadItem(
    val id: Long,
    val url: String,
    val fileName: String,
    val filePath: String?,
    val progress: Int,
    val status: DownloadStatus,

    val downloadedBytes: Long = 0L,
    val totalBytes: Long = 0L,
    val speedBytesPerSec: Long = 0L,
    val lastEtaSeconds: Long = -1L
)

enum class DownloadStatus {
    DOWNLOADING,
    PAUSED,
    SUCCESS,
    FAILED
}