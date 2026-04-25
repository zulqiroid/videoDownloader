package com.app.videodownloader.di.modules

import com.app.videodownloader.presentation.screens.appLanguage.viewModel.AppLanguageViewModel
import com.app.videodownloader.presentation.screens.splash.viewModel.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val presentationModule  = module{

    viewModel{
        SplashViewModel()
    }
    viewModel{
        AppLanguageViewModel()
    }
}