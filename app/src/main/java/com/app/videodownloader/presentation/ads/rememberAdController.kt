package com.app.videodownloader.presentation.ads

import android.app.Activity
import androidx.compose.runtime.*
import com.app.videodownloader.domain.repository.ads.AdManager

@Composable
fun rememberAdController(adManager: AdManager): AdController {
    return remember { AdController(adManager) }
}

class AdController(
    private val adManager: AdManager
) {
    private var isLoaded = false

    fun preload() {
        if (!isLoaded) {
            adManager.loadAd()
            isLoaded = true
        }
    }

    fun show(activity: Activity) {
        adManager.showAd(activity)
    }
}