package com.allvideodownloader.hdvideodownloader.securevideosaver.di.modules

import com.allvideodownloader.hdvideodownloader.securevideosaver.core.coroutines.AppCoroutineScopes
import com.allvideodownloader.hdvideodownloader.securevideosaver.core.network.provideHttpClient
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.download.VideoDownloader
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.local.dataSource.AppLanguageLocalDataSource
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.local.dataStore.AppLanguageLocalDataSourceImpl
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.local.dataStore.AppPreferences
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.local.dataStore.dataStore
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.local.dataStore.notification.NotificationSettingsLocalDataSource
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.local.dataStore.notification.NotificationSettingsLocalDataSourceImpl
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.notification.AppNotificationManager
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.remote.PlatformDetector
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.remote.ReelsApi
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.remote.DownloaderApi
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.AppLanguageRepositoryImpl
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.ads.AdRepositoryImpl
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.DataStoreRepoImpl
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.DownloaderRepositoryImpl
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.MediaFileRepositoryImpl
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.MediaRepositoryImpl
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.NotificationSettingsRepositoryImpl
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.ReelRepoImpl
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.RemoteConfigRepoImpl
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.VideoDownloadRepositoryImpl
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.ads.AppOpenAdRepositoryImpl
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.ads.InterstitialAdRepositoryImpl
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.ads.NativeAdRepositoryImpl
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.ads.UmpAdsConsentRepositoryImpl
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.AppLanguageRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads.AdRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.DataStoreRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.DownloaderRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.MediaFileRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.MediaRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.NotificationSettingsRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ReelRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.RemoteConfigRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.VideoDownloadRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads.AdsConsentRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads.AppOpenAdRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads.InterstitialAdRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads.NativeAdRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import android.content.pm.ApplicationInfo
import android.content.Context
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.notification.DownloadNotificationDispatcher
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.InMemoryDownloadProgressStore
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.appUpdate.PlayStoreAppUpdateRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.billing.GooglePlayBillingRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.billing.PremiumAccessControllerImpl
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.billing.PremiumEntitlementRepositoryImpl
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.playback.DefaultAudioEffectsController
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.playback.DefaultMediaPlaybackController
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.DownloadProgressStore
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.appUpdate.AppUpdateRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.billing.BillingRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.billing.PremiumAccessController
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.billing.PremiumEntitlementRepository
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.playback.DefaultMediaPlaybackStateStore
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.playback.DefaultPictureInPictureCommandBus
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.playback.DefaultPictureInPictureStateStore
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.AudioEffectsController
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.MediaPlaybackController
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.MediaPlaybackStateStore
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.PictureInPictureCommandBus
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.PictureInPictureStateStore
import com.allvideodownloader.hdvideodownloader.securevideosaver.framework.media.FloatingVideoPlayerCoordinator

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

    single<MediaPlaybackStateStore> {
        DefaultMediaPlaybackStateStore()
    }

    single<MediaPlaybackController> {
        DefaultMediaPlaybackController(
            context = androidContext(),
            mediaPlaybackStateStore = get(),
            applicationScope = get()
        )
    }

    single<AudioEffectsController> {
        DefaultAudioEffectsController()
    }

    single {
        FloatingVideoPlayerCoordinator(
            applicationScope = get(),
            audioEffectsController = get()
        )
    }

    single<PictureInPictureStateStore> {
        DefaultPictureInPictureStateStore()
    }

    single<PictureInPictureCommandBus> {
        DefaultPictureInPictureCommandBus()
    }
}

private fun Context.isDebugBuild(): Boolean {
    return (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
}