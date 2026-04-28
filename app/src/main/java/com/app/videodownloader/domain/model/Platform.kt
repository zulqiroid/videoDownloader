package com.app.videodownloader.domain.model

enum class Platform(val endpoint: String) {
    FACEBOOK("fbdownloader"),
    INSTAGRAM("instadownloader"),
    TIKTOK("ttdownloader"),
    THREADS("thdownloader"),
    SNACK("snackdownloader"),
    LIKEE("likeedownl")
}