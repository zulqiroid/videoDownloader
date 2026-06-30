package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads

data class AdsConsentResult(
    val canRequestAds: Boolean,
    val privacyOptionsRequired: Boolean,
    val errorMessage: String? = null
)