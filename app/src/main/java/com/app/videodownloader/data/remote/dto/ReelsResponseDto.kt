package com.app.videodownloader.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReelsResponseDto(
    val id: Int,
    val name: String,
    val thumbnail: String,
    val category_index: Int,
    val items: List<ReelItemDto>
)

@Serializable
data class ReelItemDto(
    val id: Int,
    val category: Int,
    val is_ad: Boolean,
    val is_premium: Boolean,
    val url: String,
    val is_type: String,
    val created_at: String
)