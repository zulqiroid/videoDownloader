package com.app.videodownloader.presentation.navigation
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey {

    @Serializable
    object SplashScreen : Screen()

    @Serializable
    object AppLanguageScreen : Screen()

}