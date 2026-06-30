package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.appLanguage.states

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.localization.AppLanguageCodes
import com.google.android.gms.ads.nativead.NativeAd

data class AppLanguageStates(
    val selectedLanguage: AppLanguageCodes = AppLanguageCodes.DEFAULT,
    val showExitDialogue: Boolean = false,

    val isPremiumUser: Boolean = false,

    val nativeAds: Map<String, NativeAd> = emptyMap(),
    val nativeAdConfig: NativeAdConfig = NativeAdConfig.default()
)