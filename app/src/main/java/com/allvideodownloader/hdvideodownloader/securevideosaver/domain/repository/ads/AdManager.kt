package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads

import android.app.Activity

interface AdManager {
    fun loadAd()
    fun showAd(activity: Activity)
}