package com.app.videodownloader.data.remote

import android.util.Log
import com.app.videodownloader.data.remote.dto.VideoResponseDto
import com.app.videodownloader.domain.repository.RemoteConfigRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.header
 import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType

class DownloaderApi(
    private val client: HttpClient,
    private val detector: PlatformDetector,
    private val remoteConfigRepository: RemoteConfigRepository
) {

    suspend fun download(url: String): VideoResponseDto {

        val platform = detector.detect(url)
        val baseUrl = remoteConfigRepository.getBaseUrl()
        val key= remoteConfigRepository.getApiSecretKey()

        val response = client.post(
            "https://testingdownloader.totalfreeai.com/${platform.endpoint}"
        ) {
            contentType(ContentType.Application.Json)
            header("X-Secret-Key", "I3V1T9kAd7iD0jg7ITqQLgjcZC1Nv7cyO3WZILtHsYhXVumkPj")
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("url", url)
                    }
                )
            )
        }

        val raw = response.bodyAsText()
        Log.d("Raw Responce","RAW RESPONSE: $raw")

        return response.body()
    }
}