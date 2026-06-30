package com.allvideodownloader.hdvideodownloader.securevideosaver.data.remote

import android.util.Log
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.remote.dto.ReelsResponseDto
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.RemoteConfigRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ReelsApi(
    private val client: HttpClient,
    private val remoteConfigRepository: RemoteConfigRepository
) {

    suspend fun getReels(): List<ReelsResponseDto> {

        val baseUrl = remoteConfigRepository.getBaseUrl()
        val key= remoteConfigRepository.getApiSecretKey()

        val response = client.get(
            "${baseUrl}reels/categories/"
        ) {
            contentType(ContentType.Application.Json)
            header("X-Secret-Key", key)
        }

        val raw = response.bodyAsText()
        Log.d("ReelsApi", "RAW RESPONSE: $raw")

        return response.body()
    }
}