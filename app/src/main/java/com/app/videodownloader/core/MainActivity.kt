package com.app.videodownloader.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.app.videodownloader.presentation.navigation.AppNavigation
import com.app.videodownloader.presentation.screens.appLanguage.screen.AppLanguageRootScreen
import com.app.videodownloader.presentation.screens.splash.screen.SplashScreen
import com.app.videodownloader.presentation.ui.theme.VideoDownloaderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VideoDownloaderTheme {
                AppNavigation()
//                AppLanguageRootScreen()
            }
        }
    }
}