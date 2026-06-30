package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model

data class VideoData(
    val status: Boolean,
    val title: String,
    val thumbnail: String,
    val downloadOptions: List<DownloadOption>,
    val errorMessage: List<String>?,
    val platform: String,
)

data class DownloadOption(
    val quality: String,
    val url: String
)