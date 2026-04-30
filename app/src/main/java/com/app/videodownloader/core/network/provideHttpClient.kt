package com.app.videodownloader.core.network

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun provideHttpClient(): HttpClient {
    return HttpClient(OkHttp) {

        engine {
            config {
                retryOnConnectionFailure(true)
                followRedirects(true)

                // OkHttp level timeouts (optional but recommended)
                connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 60000      // total request time
            connectTimeoutMillis = 30000      // connection time
            socketTimeoutMillis = 60000       // data read/write
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
