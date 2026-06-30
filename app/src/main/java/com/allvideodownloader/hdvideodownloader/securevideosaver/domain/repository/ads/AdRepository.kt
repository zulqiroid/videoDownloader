package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads

import android.app.Activity
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState

interface AdRepository {

    fun loadAd(isSplash: Boolean,onStateChanged: (AdState) -> Unit)

    fun showAd(activity: Activity, onStateChanged: (AdState) -> Unit)

    fun isAdReady(): Boolean
}