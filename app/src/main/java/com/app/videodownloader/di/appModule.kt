package com.app.videodownloader.di

import com.app.videodownloader.di.modules.adModule
import com.app.videodownloader.di.modules.dataModule
import com.app.videodownloader.di.modules.domainModule
import com.app.videodownloader.di.modules.presentationModule

val appModule =  listOf(
    adModule,
    dataModule,
    domainModule,
    presentationModule
)