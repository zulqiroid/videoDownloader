package com.app.videodownloader.core.network

import io.ktor.client.*
import io.ktor.client.engine.okhttp.* // Change this
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun provideHttpClient(): HttpClient {
    return HttpClient(OkHttp) { // Use OkHttp instead of Android
        engine {
            config {
                retryOnConnectionFailure(true) // Crucial for "Connection Reset"
                followRedirects(true)
            }
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }
}
