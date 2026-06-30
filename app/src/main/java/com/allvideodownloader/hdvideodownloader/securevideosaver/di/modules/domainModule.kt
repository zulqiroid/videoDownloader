package com.allvideodownloader.hdvideodownloader.securevideosaver.di.modules

 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.appUpdate.AppUpdatePolicy
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.CancelDownloadUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.DeleteMediaFileUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.FetchVideoUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.GetAudiosUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.GetDownloadedFilesUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.GetPrivacyPolicyVisibility
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.GetRCPremiumIconVisibility
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.GetRCPrivacyPolicyLink
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.GetTrendingReelsUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.GetVideosUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.MoveMediaFileUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.NotificationSettingsUseCases
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.NotificationTriggerUseCases
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ObserveDownloadsUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ObserveNotificationSettingsUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.PauseDownloadUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.RenameMediaFileUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ResumeDownloadUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.SetAudioAsRingtoneUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ShowAppUpdateNotificationUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ShowDownloadCompleteNotificationUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ShowDownloadFailedNotificationUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.StartDownloadUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.UpdateNotificationSettingsUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.AdsConsentUseCases
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.CanRequestAdsUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ClearAllNativeAdsUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ClearNativeAdUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.GetAppOpenAdConfigUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.GetBannerAdConfigUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.GetInterstitialAdConfigUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.GetNativeAdConfigUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.InitializeMobileAdsUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.IsPrivacyOptionsRequiredUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.LoadAppOpenAdUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.LoadInterstitialAdUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.LoadNativeAdUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveAppOpenAdConfigUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveBannerAdConfigUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveInterstitialAdConfigUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdConfigUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdPoolsUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdsUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.RequestAdsConsentUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ResetAdsConsentForTestingUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ShowAppOpenAdUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ShowInterstitialAdUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ShowPrivacyOptionsFormUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.appUpdate.CheckAppUpdateUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.billing.*
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.appLanguage.GetSelectedLanguageUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.appLanguage.HasSelectedLanguageUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.appLanguage.ObserveSelectedLanguageUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.appLanguage.SaveSelectedLanguageUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.firstLaunch.FirstLaunchUseCases
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.firstLaunch.GetFirstLaunchUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.firstLaunch.SetFirstLaunchUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.policy.GetPolicyAcceptedUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.policy.PolicyUseCases
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.policy.SetPolicyAcceptedUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.playback.ObserveMediaPlaybackStateUseCase
 import org.koin.dsl.module
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.playback.ApplyEqualizerPresetUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.playback.ObserveAudioEffectsStateUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.playback.ObservePictureInPictureCommandsUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.playback.ObservePictureInPictureStateUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.playback.SetBassBoostEnabledUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.playback.SetBassBoostStrengthUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.playback.SetEqualizerBandLevelUseCase
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.playback.SetEqualizerEnabledUseCase

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
        StartDownloadUseCase(get(),)
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

    single {
        ObserveNativeAdPoolsUseCase(get())
    }


    single {
        ObserveSelectedLanguageUseCase(
            repository = get()
        )
    }

    single {
        GetSelectedLanguageUseCase(
            repository = get()
        )
    }

    single {
        SaveSelectedLanguageUseCase(
            repository = get()
        )
    }

    single {
        HasSelectedLanguageUseCase(
            repository = get()
        )
    }



    single {
        ShowDownloadCompleteNotificationUseCase(
            repository = get(),
            notificationManager = get()
        )
    }

    single {
        ShowDownloadFailedNotificationUseCase(
            repository = get(),
            notificationManager = get()
        )
    }

    single {
        ShowAppUpdateNotificationUseCase(
            repository = get(),
            notificationManager = get()
        )
    }

    single {
        NotificationTriggerUseCases(
            showDownloadCompleteNotificationUseCase = get(),
            showDownloadFailedNotificationUseCase = get(),
            showAppUpdateNotificationUseCase = get()
        )
    }


    single {
        RequestAdsConsentUseCase(
            repository = get()
        )
    }

    single {
        ShowPrivacyOptionsFormUseCase(
            repository = get()
        )
    }

    single {
        CanRequestAdsUseCase(
            repository = get()
        )
    }

    single {
        IsPrivacyOptionsRequiredUseCase(
            repository = get()
        )
    }

    single {
        ResetAdsConsentForTestingUseCase(
            repository = get()
        )
    }

    single {
        AdsConsentUseCases(
            requestAdsConsentUseCase = get(),
            showPrivacyOptionsFormUseCase = get(),
            canRequestAdsUseCase = get(),
            isPrivacyOptionsRequiredUseCase = get(),
            resetAdsConsentForTestingUseCase = get()
        )
    }

    single {
        InitializeMobileAdsUseCase(
            mobileAdsInitializer = get()
        )
    }



    single {
        ConnectBillingUseCase(
            billingRepository = get()
        )
    }

    single {
        DisconnectBillingUseCase(
            billingRepository = get()
        )
    }

    single {
        ObserveBillingPlansUseCase(
            billingRepository = get()
        )
    }

    single {
        ObserveBillingConnectionStateUseCase(
            billingRepository = get()
        )
    }

    single {
        LaunchPremiumPurchaseUseCase(
            billingRepository = get()
        )
    }

    single {
        RestorePremiumPurchasesUseCase(
            billingRepository = get()
        )
    }

    single {
        ObservePremiumPurchaseResultsUseCase(
            billingRepository = get()
        )
    }

    single {
        ObservePremiumEntitlementUseCase(
            premiumEntitlementRepository = get()
        )
    }

    single {
        ObserveIsPremiumUserUseCase(
            premiumEntitlementRepository = get()
        )
    }

    single {
        UpdatePremiumEntitlementUseCase(
            premiumEntitlementRepository = get()
        )
    }

    single {
        ClearPremiumEntitlementUseCase(
            premiumEntitlementRepository = get()
        )
    }

    single {
        PremiumBillingUseCases(
            connectBillingUseCase = get(),
            disconnectBillingUseCase = get(),
            observeBillingPlansUseCase = get(),
            observeBillingConnectionStateUseCase = get(),
            launchPremiumPurchaseUseCase = get(),
            restorePremiumPurchasesUseCase = get(),
            observePremiumPurchaseResultsUseCase = get(),
            observePremiumEntitlementUseCase = get(),
            observeIsPremiumUserUseCase = get(),
            updatePremiumEntitlementUseCase = get(),
            clearPremiumEntitlementUseCase = get()
        )
    }

    single {
        GetRCPremiumIconVisibility(
            repository = get()
        )
    }
    single {
        GetPrivacyPolicyVisibility(
            repository = get()
        )
    }

    single {
        AppUpdatePolicy(
            immediateUpdatePriorityThreshold = 4,
            immediateUpdateStalenessDaysThreshold = 3
        )
    }

    single {
        CheckAppUpdateUseCase(
            appUpdateRepository = get()
        )
    }

    single {
        PauseDownloadUseCase(
            repository = get()
        )
    }

    single {
        ResumeDownloadUseCase(
            repository = get()
        )
    }

    single {
        GetRCPrivacyPolicyLink(
            repository = get()
        )
    }

    factory {
        ObserveMediaPlaybackStateUseCase(
            mediaPlaybackStateStore = get()
        )
    }

    factory {
        ObserveAudioEffectsStateUseCase(
            audioEffectsController = get()
        )
    }

    factory {
        SetEqualizerEnabledUseCase(
            audioEffectsController = get()
        )
    }

    factory {
        SetEqualizerBandLevelUseCase(
            audioEffectsController = get()
        )
    }

    factory {
        ApplyEqualizerPresetUseCase(
            audioEffectsController = get()
        )
    }

    factory {
        SetBassBoostEnabledUseCase(
            audioEffectsController = get()
        )
    }

    factory {
        SetBassBoostStrengthUseCase(
            audioEffectsController = get()
        )
    }

    factory {
        ObservePictureInPictureStateUseCase(
            pictureInPictureStateStore = get()
        )
    }

    factory {
        ObservePictureInPictureCommandsUseCase(
            pictureInPictureCommandBus = get()
        )
    }
}