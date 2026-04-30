package com.app.videodownloader.di.modules

import com.app.videodownloader.data.manager.AppOpenAdManager
import com.app.videodownloader.data.manager.InterstitialAdManager
import com.app.videodownloader.domain.repository.ads.AdManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val adModule  = module{

    single { AppOpenAdManager(androidContext()) }

    single<InterstitialAdManager> {
        InterstitialAdManager(context = androidContext())
    }
}