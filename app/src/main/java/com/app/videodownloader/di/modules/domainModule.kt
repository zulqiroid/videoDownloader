package com.app.videodownloader.di.modules

 import com.app.videodownloader.domain.usecases.CancelDownloadUseCase
 import com.app.videodownloader.domain.usecases.DeleteMediaFileUseCase
 import com.app.videodownloader.domain.usecases.FetchVideoUseCase
 import com.app.videodownloader.domain.usecases.GetAudiosUseCase
 import com.app.videodownloader.domain.usecases.GetDownloadedFilesUseCase
 import com.app.videodownloader.domain.usecases.GetTrendingReelsUseCase
 import com.app.videodownloader.domain.usecases.GetVideosUseCase
 import com.app.videodownloader.domain.usecases.MoveMediaFileUseCase
 import com.app.videodownloader.domain.usecases.NotificationSettingsUseCases
 import com.app.videodownloader.domain.usecases.ObserveDownloadsUseCase
 import com.app.videodownloader.domain.usecases.ObserveNotificationSettingsUseCase
 import com.app.videodownloader.domain.usecases.RenameMediaFileUseCase
 import com.app.videodownloader.domain.usecases.SetAudioAsRingtoneUseCase
 import com.app.videodownloader.domain.usecases.StartDownloadUseCase
 import com.app.videodownloader.domain.usecases.UpdateNotificationSettingsUseCase
 import com.app.videodownloader.domain.usecases.ads.ClearAllNativeAdsUseCase
 import com.app.videodownloader.domain.usecases.ads.ClearNativeAdUseCase
 import com.app.videodownloader.domain.usecases.ads.GetAppOpenAdConfigUseCase
 import com.app.videodownloader.domain.usecases.ads.GetBannerAdConfigUseCase
 import com.app.videodownloader.domain.usecases.ads.GetInterstitialAdConfigUseCase
 import com.app.videodownloader.domain.usecases.ads.GetNativeAdConfigUseCase
 import com.app.videodownloader.domain.usecases.ads.LoadAppOpenAdUseCase
 import com.app.videodownloader.domain.usecases.ads.LoadInterstitialAdUseCase
 import com.app.videodownloader.domain.usecases.ads.LoadNativeAdUseCase
 import com.app.videodownloader.domain.usecases.ads.ObserveAppOpenAdConfigUseCase
 import com.app.videodownloader.domain.usecases.ads.ObserveBannerAdConfigUseCase
 import com.app.videodownloader.domain.usecases.ads.ObserveInterstitialAdConfigUseCase
 import com.app.videodownloader.domain.usecases.ads.ObserveNativeAdConfigUseCase
  import com.app.videodownloader.domain.usecases.ads.ObserveNativeAdsUseCase
 import com.app.videodownloader.domain.usecases.ads.ShowAppOpenAdUseCase
 import com.app.videodownloader.domain.usecases.ads.ShowInterstitialAdUseCase
 import com.app.videodownloader.domain.usecases.dataStore.firstLaunch.FirstLaunchUseCases
import com.app.videodownloader.domain.usecases.dataStore.firstLaunch.GetFirstLaunchUseCase
import com.app.videodownloader.domain.usecases.dataStore.firstLaunch.SetFirstLaunchUseCase
import com.app.videodownloader.domain.usecases.dataStore.policy.GetPolicyAcceptedUseCase
import com.app.videodownloader.domain.usecases.dataStore.policy.PolicyUseCases
import com.app.videodownloader.domain.usecases.dataStore.policy.SetPolicyAcceptedUseCase
import org.koin.dsl.module

val domainModule = module {

    single {
        GetFirstLaunchUseCase(get())
    }

    single {
        SetFirstLaunchUseCase(get())
    }

    single {
        FirstLaunchUseCases(
            getFirstLaunch = get(),
            setFirstLaunch = get()
        )
    }


    single {
        GetPolicyAcceptedUseCase(get())
    }

    single {
        SetPolicyAcceptedUseCase(get())
    }

    single {
        PolicyUseCases(
           get(),
            get()
        )
    }

    single {
        GetTrendingReelsUseCase(get())
    }

    single {
        FetchVideoUseCase(get())
    }
/*
    factory {
        DownloadVideoUseCase(get())
    }*/
    factory {
        StartDownloadUseCase(get())
    }

    factory {
        ObserveDownloadsUseCase(get())
    }
    factory {
        GetDownloadedFilesUseCase(get())
    }
    factory {
        CancelDownloadUseCase(get())
    }
    factory {
        GetVideosUseCase(get())
    }
    factory {
        GetAudiosUseCase(get())
    }

    factory { LoadAppOpenAdUseCase(get()) }
    factory { ShowAppOpenAdUseCase(get()) }
    factory { LoadInterstitialAdUseCase(get()) }
    factory { ShowInterstitialAdUseCase(get()) }

    factory {
        ObserveNotificationSettingsUseCase(
            repository = get()
        )
    }

    factory {
        UpdateNotificationSettingsUseCase(
            repository = get()
        )
    }

    factory {
        NotificationSettingsUseCases(
            observeNotificationSettingsUseCase = get(),
            updateNotificationSettingsUseCase = get(),
        )
    }

    factory {
        RenameMediaFileUseCase(
            repository = get()
        )
    }

    factory {
        DeleteMediaFileUseCase(
            repository = get()
        )
    }

    factory {
        MoveMediaFileUseCase(
            repository = get()
        )
    }
    factory {
        SetAudioAsRingtoneUseCase(
            repository = get()
        )
    }

    single {
        ObserveAppOpenAdConfigUseCase(
            repository = get()
        )
    }

    single {
        GetAppOpenAdConfigUseCase(
            repository = get()
        )
    }

    single {
        ObserveInterstitialAdConfigUseCase(
            repository = get()
        )
    }

    single {
        GetInterstitialAdConfigUseCase(
            repository = get()
        )
    }

    single {
        ObserveBannerAdConfigUseCase(
            repository = get()
        )
    }

    single {
        GetBannerAdConfigUseCase(
            repository = get()
        )
    }

    factory { LoadNativeAdUseCase(get()) }
    factory { ObserveNativeAdsUseCase(get()) }
    factory { ClearNativeAdUseCase(get()) }
    factory { ClearAllNativeAdsUseCase(get()) }

    single {
        ObserveNativeAdConfigUseCase(
            repository = get()
        )
    }

    single {
        GetNativeAdConfigUseCase(
            repository = get()
        )
    }

}