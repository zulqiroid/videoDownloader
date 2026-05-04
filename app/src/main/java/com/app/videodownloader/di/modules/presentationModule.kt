package com.app.videodownloader.di.modules

import com.app.videodownloader.presentation.ads.banner.viewModel.BannerAdViewModel
import com.app.videodownloader.presentation.lifecycle.AppOpenAdLifecycleObserver
import com.app.videodownloader.presentation.localization.AppLocaleController
import com.app.videodownloader.presentation.screens.downloader.viewModel.DownloaderViewModel
import com.app.videodownloader.presentation.screens.appLanguage.viewModel.AppLanguageViewModel
import com.app.videodownloader.presentation.screens.download.viewModel.DownloadViewModel
import com.app.videodownloader.presentation.screens.downloadGuide.viewModel.DownloadGuideViewModel
import com.app.videodownloader.presentation.screens.home.viewModel.HomeViewModel
import com.app.videodownloader.presentation.screens.main.viewModel.MainViewModel
import com.app.videodownloader.presentation.screens.medaPlayer.viewModel.MediaPlayerViewModel
import com.app.videodownloader.presentation.screens.more.viewModel.MoreViewModel
import com.app.videodownloader.presentation.screens.onBoarding.viewModel.OnboardingViewModel
import com.app.videodownloader.presentation.screens.player.viewModel.PlayerViewModel
import com.app.videodownloader.presentation.screens.premium.viewModel.PremiumViewModel
import com.app.videodownloader.presentation.screens.reels.viewModel.ReelsViewModel
import com.app.videodownloader.presentation.screens.social.viewModel.SocialViewModel
import com.app.videodownloader.presentation.screens.splash.viewModel.SplashViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule  = module{

    viewModel {
        SplashViewModel(
            firstLaunchUseCases = get(),
            policyUseCases = get(),
            loadAppOpenAdUseCase = get(),
            showAppOpenAdUseCase = get(),
            getAppOpenAdConfigUseCase = get()
        )
    }

    viewModel {
        AppLanguageViewModel(
            loadNativeAdUseCase = get(),
            observeNativeAdsUseCase = get(),
            observeNativeAdPoolsUseCase = get(),
            observeNativeAdConfigUseCase = get(),
            getSelectedLanguageUseCase = get(),
            saveSelectedLanguageUseCase = get()
        )
    }


    viewModel {
        DownloaderViewModel(get(), get())
    }
    viewModel {
        OnboardingViewModel(
            firstLaunchUseCases = get(),
            policyUseCases = get(),
            loadNativeAdUseCase = get(),
            observeNativeAdsUseCase = get(),
            observeNativeAdConfigUseCase = get(),
            clearAllNativeAdsUseCase = get()
        )
    }
    viewModel {
        MainViewModel(get(), get(), get(), get(), get(), get(), get(),get(), get())
    }

    viewModel { HomeViewModel(get()) }

    viewModel {
        ReelsViewModel(get())
    }

    viewModel {
        DownloadViewModel(
            observeDownloadsUseCase = get(),
            getDownloadedFilesUseCase = get(),
            cancelDownloadUseCase = get(),
            loadNativeAdUseCase = get(),
            observeNativeAdPoolsUseCase = get(),
            observeNativeAdConfigUseCase = get()
        )
    }
    viewModel {
        PlayerViewModel(
            getVideos = get(),
            getAudios = get(),
            loadNativeAdUseCase = get(),
            observeNativeAdPoolsUseCase = get(),
            observeNativeAdConfigUseCase = get()
        )
    }

    viewModel {
        MoreViewModel(
            loadNativeAdUseCase = get(),
            observeNativeAdsUseCase = get(),
            observeNativeAdConfigUseCase = get()
        )
    }

    viewModel {
        MediaPlayerViewModel(
            application = androidApplication(),
            renameMediaFileUseCase = get(),
            deleteMediaFileUseCase = get(),
            setAudioAsRingtoneUseCase = get(),
            loadNativeAdUseCase = get(),
            observeNativeAdPoolsUseCase = get(),
            observeNativeAdConfigUseCase = get()
        )
    }

    viewModel {
        SocialViewModel(
            get()
        )
    }
    viewModel {
        DownloadGuideViewModel()
    }
    viewModel {
        PremiumViewModel()
    }

    single {
        AppOpenAdLifecycleObserver(
            appOpenAdRepository = get()
        )
    }

    viewModel {
        BannerAdViewModel(
            observeBannerAdConfigUseCase = get()
        )
    }

    single {
        AppLocaleController(
            observeSelectedLanguage = get(),
            applicationScope = get()
        )
    }
}