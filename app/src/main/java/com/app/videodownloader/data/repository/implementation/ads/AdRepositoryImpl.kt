package com.app.videodownloader.data.repository.implementation.ads

import android.app.Activity
import com.app.videodownloader.data.manager.AppOpenAdManager
import com.app.videodownloader.domain.model.ads.AdState
import com.app.videodownloader.domain.repository.ads.AdRepository

 class AdRepositoryImpl(
    private val adManager: AppOpenAdManager
) : AdRepository {

    override fun loadAd(onStateChanged: (AdState) -> Unit) {
        adManager.loadAd(onStateChanged)
    }

    override fun showAd(activity: Activity, onStateChanged: (AdState) -> Unit) {
        //adManager.showAd(activity, onStateChanged)
    }

    override fun isAdReady(): Boolean =
        adManager.isAdReady()
}