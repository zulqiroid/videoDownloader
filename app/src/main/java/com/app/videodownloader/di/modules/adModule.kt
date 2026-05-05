package com.app.videodownloader.di.modules

import com.app.videodownloader.data.manager.AppOpenAdManager
import com.app.videodownloader.data.manager.FullScreenAdCoordinator
import com.app.videodownloader.data.manager.InterstitialAdManager
import com.app.videodownloader.data.manager.MobileAdsInitializer
import com.app.videodownloader.data.manager.NativeAdManager
import com.app.videodownloader.domain.model.ads.AppOpenAdConfig
import com.app.videodownloader.domain.repository.ads.AdManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val adModule  = module{


    single {
        AppOpenAdManager(
            context = androidContext(),
            observeAppOpenAdConfigUseCase = get(),
            canRequestAdsUseCase = get(),
            fullScreenAdCoordinator = get(),
            premiumAccessController = get()
        )
    }

    single {
        InterstitialAdManager(
            context = androidContext(),
            observeInterstitialAdConfigUseCase = get(),
            canRequestAdsUseCase = get(),
            fullScreenAdCoordinator = get(),
            premiumAccessController = get()
        )
    }

    single {
        NativeAdManager(
            context = androidContext(),
            observeNativeAdConfigUseCase = get(),
            canRequestAdsUseCase = get(),
            premiumAccessController = get()
        )
    }

    single {
        MobileAdsInitializer(
            context = androidContext()
        )
    }

    single {
        FullScreenAdCoordinator()
    }
}