package com.app.videodownloader.domain.repository.ads

import android.app.Activity

interface AdManager {
    fun loadAd()
    fun showAd(activity: Activity)
}