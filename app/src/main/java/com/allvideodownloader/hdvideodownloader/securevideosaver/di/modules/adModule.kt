package com.allvideodownloader.hdvideodownloader.securevideosaver.di.modules

import com.allvideodownloader.hdvideodownloader.securevideosaver.data.manager.AppOpenAdManager
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.manager.FullScreenAdCoordinator
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.manager.InterstitialAdManager
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.manager.MobileAdsInitializer
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.manager.NativeAdManager
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