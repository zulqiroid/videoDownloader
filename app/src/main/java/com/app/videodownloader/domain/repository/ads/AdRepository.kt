package com.app.videodownloader.domain.repository.ads

import android.app.Activity
import com.app.videodownloader.domain.model.ads.AdState

interface AdRepository {

    fun loadAd(onStateChanged: (AdState) -> Unit)

    fun showAd(activity: Activity, onStateChanged: (AdState) -> Unit)

    fun isAdReady(): Boolean
}