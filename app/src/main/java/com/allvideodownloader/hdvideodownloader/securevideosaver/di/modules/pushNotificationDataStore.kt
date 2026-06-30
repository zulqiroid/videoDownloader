package com.allvideodownloader.hdvideodownloader.securevideosaver.di.modules

import com.allvideodownloader.hdvideodownloader.securevideosaver.data.local.dataStore.push.FcmTokenLocalDataSource
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.remote.push.FirebasePushNotificationRemoteDataSource
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.remote.push.PushNotificationRemoteDataSource
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.push.PushNotificationRepositoryImpl
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.push.PushNotificationRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.push.FetchCurrentFcmTokenUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.push.InitializePushNotificationsUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.push.SaveFcmTokenUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.push.SyncFcmTokenUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.framework.push.AndroidAppNotificationManager
import com.allvideodownloader.hdvideodownloader.securevideosaver.framework.push.AppNotificationManager
import com.allvideodownloader.hdvideodownloader.securevideosaver.framework.push.FcmTopicManager
import com.allvideodownloader.hdvideodownloader.securevideosaver.framework.push.FirebaseFcmTopicManager
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val pushNotificationModule = module {

    single {
        FirebaseMessaging.getInstance()
    }

    single {
        FirebaseInstallations.getInstance()
    }

    single {
        FirebaseFunctions.getInstance(FIREBASE_FUNCTIONS_REGION)
    }

    single {
        FcmTokenLocalDataSource(
            dataStore = get()
        )
    }

    single<PushNotificationRemoteDataSource> {
        FirebasePushNotificationRemoteDataSource(
            firebaseFunctions = get()
        )
    }

    single<PushNotificationRepository> {
        PushNotificationRepositoryImpl(
            context = androidContext(),
            firebaseMessaging = get(),
            firebaseInstallations = get(),
            localDataSource = get(),
            remoteDataSource = get()
        )
    }

    single<AppNotificationManager> {
        AndroidAppNotificationManager(
            context = androidContext()
        )
    }

    factory {
        FetchCurrentFcmTokenUseCase(
            repository = get()
        )
    }

    factory {
        SaveFcmTokenUseCase(
            repository = get()
        )
    }

    factory {
        SyncFcmTokenUseCase(
            repository = get()
        )
    }

    factory {
        InitializePushNotificationsUseCase(
            repository = get()
        )
    }

    single<FcmTopicManager> {
        FirebaseFcmTopicManager(
            firebaseMessaging = get()
        )
    }
}

private const val FIREBASE_FUNCTIONS_REGION = "asia-south1"