package com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.ads

import android.app.Activity
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.manager.AppOpenAdManager
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads.AdRepository

 class AdRepositoryImpl(
    private val adManager: AppOpenAdManager
) : AdRepository {

    override fun loadAd(isSplash: Boolean, onStateChanged: (AdState) -> Unit) {
        adManager.loadAd(isSplash = isSplash,onStateChanged = onStateChanged)
    }

    override fun showAd(activity: Activity, onStateChanged: (AdState) -> Unit) {
        //adManager.showAd(activity, onStateChanged)
    }

    override fun isAdReady(): Boolean =
        adManager.isAdReady()
}