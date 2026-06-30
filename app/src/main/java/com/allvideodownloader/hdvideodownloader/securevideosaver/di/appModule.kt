package com.allvideodownloader.hdvideodownloader.securevideosaver.di

import com.allvideodownloader.hdvideodownloader.securevideosaver.di.modules.adModule
import com.allvideodownloader.hdvideodownloader.securevideosaver.di.modules.dataModule
import com.allvideodownloader.hdvideodownloader.securevideosaver.di.modules.domainModule
import com.allvideodownloader.hdvideodownloader.securevideosaver.di.modules.presentationModule
import com.allvideodownloader.hdvideodownloader.securevideosaver.di.modules.pushNotificationModule

val appModule =  listOf(
    adModule,
    pushNotificationModule,
    dataModule,
    domainModule,
    presentationModule
)