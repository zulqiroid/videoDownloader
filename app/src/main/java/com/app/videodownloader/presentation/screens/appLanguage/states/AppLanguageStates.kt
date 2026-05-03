package com.app.videodownloader.presentation.screens.appLanguage.states

import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.presentation.localization.AppLanguageCodes
import com.google.android.gms.ads.nativead.NativeAd

data class AppLanguageStates(
    val selectedLanguage: AppLanguageCodes = AppLanguageCodes.ENGLISH,
    val showExitDialogue: Boolean = false,

    // ✅ ADD THESE
    val nativeAds: Map<String, NativeAd> = emptyMap(),
    val nativeAdConfig: NativeAdConfig = NativeAdConfig.default()
)