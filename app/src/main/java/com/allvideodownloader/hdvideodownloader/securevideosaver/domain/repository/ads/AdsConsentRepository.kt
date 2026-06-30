package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads

import android.app.Activity
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdsConsentResult

interface AdsConsentRepository {

    suspend fun requestConsent(
        activity: Activity
    ): AdsConsentResult

    suspend fun showPrivacyOptionsForm(
        activity: Activity
    ): AdsConsentResult

    fun canRequestAds(): Boolean

    fun isPrivacyOptionsRequired(): Boolean

    fun resetConsentForTesting()
}