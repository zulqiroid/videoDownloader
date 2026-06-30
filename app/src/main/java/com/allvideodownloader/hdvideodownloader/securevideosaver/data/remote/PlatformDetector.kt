package com.allvideodownloader.hdvideodownloader.securevideosaver.data.remote

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.Platform

class PlatformDetector {

    fun detect(url: String): Platform {

        val u = url.lowercase()

        return when {
            "facebook.com" in u || "fb.watch" in u -> Platform.FACEBOOK
            "instagram.com" in u -> Platform.INSTAGRAM
            "tiktok.com" in u -> Platform.TIKTOK
            "threads" in u -> Platform.THREADS
            "snackvideo" in u || "sck.io" in u -> Platform.SNACK
            "likee" in u -> Platform.LIKEE
            else -> throw IllegalArgumentException("Unsupported platform")
        }
    }
}