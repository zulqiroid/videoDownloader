package com.app.videodownloader.domain.repository

import com.app.videodownloader.domain.model.ApiKey

interface RemoteConfigRepository {
    suspend fun initializeAndFetch(onComplete: () -> Unit)
    suspend fun getApiSecretKey(): ApiKey
    suspend fun getBaseUrl(): String
}