package com.app.videodownloader.di.modules

import com.app.videodownloader.core.network.provideHttpClient
import com.app.videodownloader.data.download.VideoDownloader
import com.app.videodownloader.data.local.dataStore.AppPreferences
import com.app.videodownloader.data.local.dataStore.dataStore
import com.app.videodownloader.data.remote.PlatformDetector
import com.app.videodownloader.data.remote.ReelsApi
import com.app.videodownloader.data.remote.DownloaderApi
import com.app.videodownloader.data.repository.implementation.DataStoreRepoImpl
import com.app.videodownloader.data.repository.implementation.DownloaderRepositoryImpl
import com.app.videodownloader.data.repository.implementation.ReelRepoImpl
import com.app.videodownloader.data.repository.implementation.RemoteConfigRepoImpl
import com.app.videodownloader.data.repository.implementation.VideoDownloadRepositoryImpl
import com.app.videodownloader.domain.repository.DataStoreRepository
import com.app.videodownloader.domain.repository.DownloaderRepository
import com.app.videodownloader.domain.repository.ReelRepository
import com.app.videodownloader.domain.repository.RemoteConfigRepository
import com.app.videodownloader.domain.repository.VideoDownloadRepository
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



}