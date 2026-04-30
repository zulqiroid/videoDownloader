package com.app.videodownloader.di.modules

import com.app.videodownloader.presentation.screens.downloader.viewModel.DownloaderViewModel
import com.app.videodownloader.presentation.screens.appLanguage.viewModel.AppLanguageViewModel
import com.app.videodownloader.presentation.screens.download.viewModel.DownloadViewModel
import com.app.videodownloader.presentation.screens.downloadGuide.viewModel.DownloadGuideViewModel
import com.app.videodownloader.presentation.screens.home.viewModel.HomeViewModel
import com.app.videodownloader.presentation.screens.main.viewModel.MainViewModel
import com.app.videodownloader.presentation.screens.medaPlayer.viewModel.MediaPlayerViewModel
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

    viewModel{
        SplashViewModel(
            get(),
            get(),get(),
            get(),
        )
    }

    viewModel{
        AppLanguageViewModel()
    }

    viewModel {
        DownloaderViewModel(get(), get())
    }
    viewModel {
        OnboardingViewModel(
            get(),
            get(),
        )
    }
    viewModel {
        MainViewModel(get(),get(), get(),get(),get(),)
    }

    viewModel { HomeViewModel(get()) }

    viewModel {
        ReelsViewModel(get())
    }

    viewModel {
        DownloadViewModel(get(),get(),get(),)
    }
    viewModel {
        PlayerViewModel(get(),get(),)
    }
    viewModel {
        MediaPlayerViewModel(
            application = androidApplication()
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
}