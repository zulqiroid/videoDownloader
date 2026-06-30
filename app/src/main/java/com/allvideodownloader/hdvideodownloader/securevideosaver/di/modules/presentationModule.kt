package com.allvideodownloader.hdvideodownloader.securevideosaver.di.modules

import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.banner.viewModel.BannerAdViewModel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.lifecycle.AppOpenAdLifecycleObserver
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.localization.AppLocaleController
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.downloader.viewModel.DownloaderViewModel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.appLanguage.viewModel.AppLanguageViewModel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.download.viewModel.DownloadViewModel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.downloadGuide.viewModel.DownloadGuideViewModel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.home.viewModel.HomeViewModel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.viewModel.MainViewModel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.viewModel.MediaPlayerViewModel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.more.viewModel.MoreViewModel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.onBoarding.viewModel.OnboardingViewModel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.viewModel.PlayerViewModel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.premium.viewModel.PremiumViewModel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.reels.viewModel.ReelsViewModel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.social.viewModel.SocialViewModel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.splash.viewModel.SplashViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    viewModel {
        SplashViewModel(
            firstLaunchUseCases = get(),
            policyUseCases = get(),
            adsConsentUseCases = get(),
            initializeMobileAdsUseCase = get(),
            loadAppOpenAdUseCase = get(),
            showAppOpenAdUseCase = get(),
            getAppOpenAdConfigUseCase = get(),
            loadInterstitialAdUseCase = get(),
            showInterstitialAdUseCase = get(),
            observeIsPremiumUserUseCase = get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }

    viewModel {
        AppLanguageViewModel(
            loadNativeAdUseCase = get(),
            observeNativeAdsUseCase = get(),
            observeNativeAdConfigUseCase = get(),
            getSelectedLanguageUseCase = get(),
            saveSelectedLanguageUseCase = get(),
            observeIsPremiumUserUseCase = get(),
            get(),
            get(),
            get(),
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
            clearAllNativeAdsUseCase = get(),
            observeIsPremiumUserUseCase = get(),
            get(),
            get(),
            get(),
        )
    }
    viewModel {
        MainViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }

    viewModel { HomeViewModel(get(), get(), get(), get(), get()) }

    viewModel {
        ReelsViewModel(get(), get(), get(), get(), get())
    }

    viewModel {
        DownloadViewModel(
            observeDownloadsUseCase = get(),
            getDownloadedFilesUseCase = get(),
            pauseDownloadUseCase = get(),
            resumeDownloadUseCase = get(),
            cancelDownloadUseCase = get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModel {
        PlayerViewModel(
            getVideos = get(),
            getAudios = get(),
            get(),
            get(),
            get(),
            get()
        )
    }

    viewModel {
        MoreViewModel(
            loadNativeAdUseCase = get(),
            observeNativeAdsUseCase = get(),
            observeNativeAdConfigUseCase = get(),
            observeIsPremiumUserUseCase = get(),
            get(),
            get(),
        )
    }

    viewModel {
        MediaPlayerViewModel(
            application = androidApplication(),
            renameMediaFileUseCase = get(),
            deleteMediaFileUseCase = get(),
            setAudioAsRingtoneUseCase = get(),
            loadNativeAdUseCase = get(),
           get(),
             get(),
             get(),
             get(),
             get(),
             get(),
             get(),
             get(),
             get(),
             get(),
             get(),
             get(),
             get(),
             get(),
        )
    }

    viewModel {
        SocialViewModel(
            get()
        )
    }
    viewModel {
        DownloadGuideViewModel(
            get(),
            get(),
            get(),
            get(),
        )
    }
    viewModel {
        PremiumViewModel(
            get(),
            get(),
            get(),
        )
    }

    single {
        AppOpenAdLifecycleObserver(
            appOpenAdRepository = get(),
            canRequestAdsUseCase = get(),
            fullScreenAdCoordinator = get()
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