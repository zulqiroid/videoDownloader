package com.app.videodownloader.data.remote

import android.util.Log
import com.app.videodownloader.data.remote.dto.ReelsResponseDto
import com.app.videodownloader.data.remote.dto.VideoResponseDto
import com.app.videodownloader.domain.repository.RemoteConfigRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType

class ReelsApi(
    private val client: HttpClient,
    private val remoteConfigRepository: RemoteConfigRepository
) {

    suspend fun getReels(): List<ReelsResponseDto> {

        val baseUrl = remoteConfigRepository.getBaseUrl()
        val key= remoteConfigRepository.getApiSecretKey()

        val response = client.get(
            "https://testingdownloader.totalfreeai.com/reels/categories/"
        ) {
            contentType(ContentType.Application.Json)
            header("X-Secret-Key", "I3V1T9kAd7iD0jg7ITqQLgjcZC1Nv7cyO3WZILtHsYhXVumkPj")
        }

        val raw = response.bodyAsText()
        Log.d("ReelsApi", "RAW RESPONSE: $raw")

        return response.body()
    }
}