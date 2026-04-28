package com.app.videodownloader.domain.model

data class Reel(
    val id: String,
    val videoUrl: String
)

data class ReelCategory(
    val id: Int,
    val name: String,
    val thumbnail: String,
    val reels: List<Reel>
)