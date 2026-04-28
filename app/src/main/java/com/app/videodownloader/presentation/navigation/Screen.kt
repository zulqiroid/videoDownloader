package com.app.videodownloader.presentation.navigation
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey {

    @Serializable
    object Splash : Screen()

    @Serializable
    object AppLanguage : Screen()

    @Serializable
    object OnBoarding : Screen()

    @Serializable
    object Main : Screen()
}