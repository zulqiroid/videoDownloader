package com.allvideodownloader.hdvideodownloader.securevideosaver.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class VideoResponseDto(
    val status: Boolean,
    val title: String? = null,
    val image_url: String? = null,
    val downloadables: List<DownloadableDto> = emptyList(),
    val error_message: List<String>? = null,
    val platform: String,
)

@Serializable
data class DownloadableDto(
    val quality: String,
    val url: String
)