package com.app.videodownloader.di.modules

import com.app.videodownloader.domain.usecases.DownloadVideoUseCase
import com.app.videodownloader.domain.usecases.FetchVideoUseCase
import com.app.videodownloader.domain.usecases.GetTrendingReelsUseCase
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

    factory {
        DownloadVideoUseCase(get())
    }
}