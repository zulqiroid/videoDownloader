package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.push

data class PushMessage(
    val title: String,
    val body: String,
    val imageUrl: String? = null,
    val deepLink: String? = null,
    val type: String? = null,
    val payload: Map<String, String> = emptyMap()
)