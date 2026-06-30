package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.splash.states

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.splash.events.SplashNavEvents
import com.google.android.gms.ads.nativead.NativeAd

data class SplashUiStates(
    val showExitDialogue: Boolean = false,
    val isStarting: Boolean = false,
    val consentErrorMessage: String? = null,
    val isConsentReady: Boolean = false,
    val pendingDestination: SplashNavEvents? = null,

    val isCheckingAppUpdate: Boolean = false,
    val appUpdateErrorMessage: String? = null,

    val isPremiumUser: Boolean = false,

    val nativeAds: Map<String, NativeAd> = emptyMap(),
    val nativeAdConfig: NativeAdConfig = NativeAdConfig.default()
)