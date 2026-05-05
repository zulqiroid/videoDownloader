package com.app.videodownloader.di.modules

import com.app.videodownloader.core.coroutines.AppCoroutineScopes
import com.app.videodownloader.core.network.provideHttpClient
import com.app.videodownloader.data.download.VideoDownloader
import com.app.videodownloader.data.local.dataSource.AppLanguageLocalDataSource
import com.app.videodownloader.data.local.dataStore.AppLanguageLocalDataSourceImpl
import com.app.videodownloader.data.local.dataStore.AppPreferences
import com.app.videodownloader.data.local.dataStore.dataStore
import com.app.videodownloader.data.local.dataStore.notification.NotificationSettingsLocalDataSource
import com.app.videodownloader.data.local.dataStore.notification.NotificationSettingsLocalDataSourceImpl
import com.app.videodownloader.data.manager.AppOpenAdManager
import com.app.videodownloader.data.notification.AppNotificationManager
import com.app.videodownloader.data.remote.PlatformDetector
import com.app.videodownloader.data.remote.ReelsApi
import com.app.videodownloader.data.remote.DownloaderApi
import com.app.videodownloader.data.repository.implementation.AppLanguageRepositoryImpl
import com.app.videodownloader.data.repository.implementation.ads.AdRepositoryImpl
import com.app.videodownloader.data.repository.implementation.DataStoreRepoImpl
import com.app.videodownloader.data.repository.implementation.DownloaderRepositoryImpl
import com.app.videodownloader.data.repository.implementation.MediaFileRepositoryImpl
import com.app.videodownloader.data.repository.implementation.MediaRepositoryImpl
import com.app.videodownloader.data.repository.implementation.NotificationSettingsRepositoryImpl
import com.app.videodownloader.data.repository.implementation.ReelRepoImpl
import com.app.videodownloader.data.repository.implementation.RemoteConfigRepoImpl
import com.app.videodownloader.data.repository.implementation.VideoDownloadRepositoryImpl
import com.app.videodownloader.data.repository.implementation.ads.AppOpenAdRepositoryImpl
import com.app.videodownloader.data.repository.implementation.ads.InterstitialAdRepositoryImpl
import com.app.videodownloader.data.repository.implementation.ads.NativeAdRepositoryImpl
import com.app.videodownloader.data.repository.implementation.ads.UmpAdsConsentRepositoryImpl
import com.app.videodownloader.domain.repository.AppLanguageRepository
import com.app.videodownloader.domain.repository.ads.AdManager
import com.app.videodownloader.domain.repository.ads.AdRepository
import com.app.videodownloader.domain.repository.DataStoreRepository
import com.app.videodownloader.domain.repository.DownloaderRepository
import com.app.videodownloader.domain.repository.MediaFileRepository
import com.app.videodownloader.domain.repository.MediaRepository
import com.app.videodownloader.domain.repository.NotificationSettingsRepository
import com.app.videodownloader.domain.repository.ReelRepository
import com.app.videodownloader.domain.repository.RemoteConfigRepository
import com.app.videodownloader.domain.repository.VideoDownloadRepository
import com.app.videodownloader.domain.repository.ads.AdsConsentRepository
import com.app.videodownloader.domain.repository.ads.AppOpenAdRepository
import com.app.videodownloader.domain.repository.ads.InterstitialAdRepository
import com.app.videodownloader.domain.repository.ads.NativeAdRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import android.content.pm.ApplicationInfo
import android.content.Context
import com.app.videodownloader.data.notification.DownloadNotificationDispatcher
import com.app.videodownloader.data.repository.implementation.InMemoryDownloadProgressStore
import com.app.videodownloader.data.repository.implementation.appUpdate.PlayStoreAppUpdateRepository
import com.app.videodownloader.data.repository.implementation.billing.GooglePlayBillingRepository
import com.app.videodownloader.data.repository.implementation.billing.PremiumAccessControllerImpl
import com.app.videodownloader.data.repository.implementation.billing.PremiumEntitlementRepositoryImpl
import com.app.videodownloader.domain.model.DownloadProgressStore
import com.app.videodownloader.domain.repository.appUpdate.AppUpdateRepository
import com.app.videodownloader.domain.repository.billing.BillingRepository
import com.app.videodownloader.domain.repository.billing.PremiumAccessController
import com.app.videodownloader.domain.repository.billing.PremiumEntitlementRepository
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


val dataModule = module{

    single {
        androidContext().dataStore
    }


    single {
        OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    // HttpClient (singleton)
    single { provideHttpClient() }

    single { PlatformDetector() }

    single<RemoteConfigRepository> {
        RemoteConfigRepoImpl(get())
    }
    // API
    single { DownloaderApi(get(), get(), get()) }

    single { VideoDownloader(get(), get()) }


    single { AppPreferences(get()) }

    single<DataStoreRepository>{ DataStoreRepoImpl(get()) }

    single { ReelsApi(get(),get(),) }

    single <ReelRepository>{ ReelRepoImpl(get()) }

    single <DownloaderRepository>{ DownloaderRepositoryImpl(get()) }

    single<DownloadProgressStore> {
        InMemoryDownloadProgressStore()
    }

    single<VideoDownloadRepository> {
        VideoDownloadRepositoryImpl(
            context = androidContext(),
            progressStore = get()
        )
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

    single<NotificationSettingsLocalDataSource> {
        NotificationSettingsLocalDataSourceImpl(
            dataStore = get()
        )
    }

    single<NotificationSettingsRepository> {
        NotificationSettingsRepositoryImpl(
            localDataSource = get()
        )
    }

    single<MediaFileRepository> {
        MediaFileRepositoryImpl(get())
    }


    single<AppOpenAdRepository> {
        AppOpenAdRepositoryImpl(
            appOpenAdManager = get()
        )
    }

    single<NativeAdRepository> {
        NativeAdRepositoryImpl(
            nativeAdManager = get()
        )
    }

    single {
        AppCoroutineScopes.applicationScope
    }

    single<AppLanguageLocalDataSource> {
        AppLanguageLocalDataSourceImpl(
            dataStore = get()
        )
    }

    single<AppLanguageRepository> {
        AppLanguageRepositoryImpl(
            localDataSource = get()
        )
    }

    single {
        AppNotificationManager(
            context = androidContext()
        )
    }



    single<AdsConsentRepository> {
        UmpAdsConsentRepositoryImpl(
            context = androidContext(),
            isDebug = androidContext().isDebugBuild()
        )
    }

    single<PremiumEntitlementRepository> {
        PremiumEntitlementRepositoryImpl(
            dataStore = get()
        )
    }

    single<BillingRepository> {
        GooglePlayBillingRepository(
            context = androidContext(),
            premiumEntitlementRepository = get()
        )
    }

    single<PremiumAccessController> {
        PremiumAccessControllerImpl(
            observeIsPremiumUserUseCase = get(),
            applicationScope = get()
        )
    }

    single<AppUpdateRepository> {
        PlayStoreAppUpdateRepository(
            context = androidContext(),
            policy = get()
        )
    }

    single {
        DownloadNotificationDispatcher(
            notificationSettingsRepository = get(),
            appNotificationManager = get()
        )
    }

}

private fun Context.isDebugBuild(): Boolean {
    return (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
}