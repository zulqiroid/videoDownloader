package com.app.videodownloader.di.modules

 import com.app.videodownloader.domain.usecases.CancelDownloadUseCase
 import com.app.videodownloader.domain.usecases.FetchVideoUseCase
 import com.app.videodownloader.domain.usecases.GetAudiosUseCase
 import com.app.videodownloader.domain.usecases.GetDownloadedFilesUseCase
 import com.app.videodownloader.domain.usecases.GetTrendingReelsUseCase
 import com.app.videodownloader.domain.usecases.GetVideosUseCase
 import com.app.videodownloader.domain.usecases.ObserveDownloadsUseCase
 import com.app.videodownloader.domain.usecases.StartDownloadUseCase
 import com.app.videodownloader.domain.usecases.ads.LoadAppOpenAdUseCase
 import com.app.videodownloader.domain.usecases.ads.LoadInterstitialAdUseCase
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
}