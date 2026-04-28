package com.app.videodownloader.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {

    val isFirstLaunch: Flow<Boolean>
    suspend fun setFirstLaunch(value: Boolean)


    val isPolicyAccepted: Flow<Boolean>
    suspend fun setPolicyAccepted(value: Boolean)

}