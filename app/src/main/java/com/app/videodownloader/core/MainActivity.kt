package com.app.videodownloader.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.app.videodownloader.domain.repository.ads.AdManager
import com.app.videodownloader.presentation.componants.privacyPolicyDialgue.PrivacyDialogHost
import com.app.videodownloader.presentation.navigation.AppNavigation
import com.app.videodownloader.presentation.screens.downloader.screen.DownloaderScreen
import com.app.videodownloader.presentation.screens.main.events.MainNavEvents
import com.app.videodownloader.presentation.screens.main.screen.MainScreen
import com.app.videodownloader.presentation.screens.main.viewModel.MainViewModel
import com.app.videodownloader.presentation.screens.onBoarding.screen.OnboardingScreen
import com.app.videodownloader.presentation.ui.theme.VideoDownloaderTheme
import kotlinx.coroutines.delay
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VideoDownloaderTheme {
                AppNavigation()
            }
        }
    }
}