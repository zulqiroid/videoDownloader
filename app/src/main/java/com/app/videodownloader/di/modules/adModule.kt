package com.app.videodownloader.di.modules

import com.app.videodownloader.data.manager.AppOpenAdManager
import com.app.videodownloader.data.manager.InterstitialAdManager
import com.app.videodownloader.data.manager.NativeAdManager
import com.app.videodownloader.domain.model.ads.AppOpenAdConfig
import com.app.videodownloader.domain.repository.ads.AdManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val adModule  = module{


    single {
        InterstitialAdManager(
            context = androidContext(),
            observeInterstitialAdConfigUseCase = get()
        )
    }

    single {
        AppOpenAdManager(
            context = androidContext(),
            observeAppOpenAdConfigUseCase = get()
        )
    }

    single {
        NativeAdManager(
            context = androidContext(),
            observeNativeAdConfigUseCase = get()
        )
    }

}