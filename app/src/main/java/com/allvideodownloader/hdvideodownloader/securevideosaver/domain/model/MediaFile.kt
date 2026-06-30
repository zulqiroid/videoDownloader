package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MediaFile(
    val id: Long,
    val filePath: String,
    val fileName: String,
    val isVideo: Boolean,
    val contentUri: String
)