package com.app.videodownloader.di

import com.app.videodownloader.di.modules.dataModule
import com.app.videodownloader.di.modules.domainModule
import com.app.videodownloader.di.modules.presentationModule

val appModule =  listOf(
    dataModule,
    domainModule,
    presentationModule
)