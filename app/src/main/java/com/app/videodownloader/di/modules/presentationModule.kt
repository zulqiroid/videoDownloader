package com.app.videodownloader.di.modules

import com.app.videodownloader.presentation.screens.downloader.viewModel.DownloaderViewModel
import com.app.videodownloader.presentation.screens.appLanguage.viewModel.AppLanguageViewModel
import com.app.videodownloader.presentation.screens.home.viewModel.HomeViewModel
import com.app.videodownloader.presentation.screens.main.viewModel.MainViewModel
import com.app.videodownloader.presentation.screens.onBoarding.viewModel.OnboardingViewModel
import com.app.videodownloader.presentation.screens.reels.viewModel.ReelsViewModel
import com.app.videodownloader.presentation.screens.splash.viewModel.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule  = module{

    viewModel{
        SplashViewModel(
            get(),
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
        MainViewModel(get(),get(), get())
    }

    viewModel { HomeViewModel(get()) }

    viewModel {
        ReelsViewModel(get())
    }
}