package com.app.videodownloader.di.modules

import com.app.videodownloader.core.network.provideHttpClient
import com.app.videodownloader.data.download.VideoDownloader
import com.app.videodownloader.data.local.dataStore.AppPreferences
import com.app.videodownloader.data.local.dataStore.dataStore
import com.app.videodownloader.data.manager.AppOpenAdManager
import com.app.videodownloader.data.remote.PlatformDetector
import com.app.videodownloader.data.remote.ReelsApi
import com.app.videodownloader.data.remote.DownloaderApi
import com.app.videodownloader.data.repository.implementation.ads.AdRepositoryImpl
import com.app.videodownloader.data.repository.implementation.DataStoreRepoImpl
import com.app.videodownloader.data.repository.implementation.DownloaderRepositoryImpl
import com.app.videodownloader.data.repository.implementation.MediaRepositoryImpl
import com.app.videodownloader.data.repository.implementation.ReelRepoImpl
import com.app.videodownloader.data.repository.implementation.RemoteConfigRepoImpl
import com.app.videodownloader.data.repository.implementation.VideoDownloadRepositoryImpl
import com.app.videodownloader.data.repository.implementation.ads.InterstitialAdRepositoryImpl
import com.app.videodownloader.domain.repository.ads.AdManager
import com.app.videodownloader.domain.repository.ads.AdRepository
import com.app.videodownloader.domain.repository.DataStoreRepository
import com.app.videodownloader.domain.repository.DownloaderRepository
import com.app.videodownloader.domain.repository.MediaRepository
import com.app.videodownloader.domain.repository.ReelRepository
import com.app.videodownloader.domain.repository.RemoteConfigRepository
import com.app.videodownloader.domain.repository.VideoDownloadRepository
import com.app.videodownloader.domain.repository.ads.InterstitialAdRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module{

    single {
        androidContext().dataStore
    }


    // HttpClient (singleton)
    single { provideHttpClient() }

    single { PlatformDetector() }

    single<RemoteConfigRepository> {
        RemoteConfigRepoImpl()
    }
    // API
    single { DownloaderApi(get(), get(), get()) }

    single { VideoDownloader(get(), get()) }


    single { AppPreferences(get()) }

    single<DataStoreRepository>{ DataStoreRepoImpl(get()) }

    single { ReelsApi(get(),get(),) }

    single <ReelRepository>{ ReelRepoImpl(get()) }

    single <DownloaderRepository>{ DownloaderRepositoryImpl(get()) }

    single<VideoDownloadRepository> {
        VideoDownloadRepositoryImpl(get())
    }

    single<MediaRepository>{ MediaRepositoryImpl(get()) }

    single <AdRepository>{
        AdRepositoryImpl(
            get()
        )
    }
    single <InterstitialAdRepository>{
        InterstitialAdRepositoryImpl(
            get()
        )
    }
}