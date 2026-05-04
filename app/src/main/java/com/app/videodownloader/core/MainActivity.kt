package com.app.videodownloader.core

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.videodownloader.presentation.localization.AppLocaleController
import com.app.videodownloader.presentation.localization.BlockingLocaleReader
import com.app.videodownloader.presentation.localization.LocaleContextWrapper
import com.app.videodownloader.presentation.navigation.AppNavigation
import com.app.videodownloader.presentation.ui.theme.VideoDownloaderTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val appLocaleController: AppLocaleController by inject()

    override fun attachBaseContext(newBase: Context) {
        val selectedLanguage = BlockingLocaleReader.readLanguage(newBase)

        val localizedContext = LocaleContextWrapper.wrap(
            context = newBase,
            language = selectedLanguage
        )

        super.attachBaseContext(localizedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val selectedLanguage by appLocaleController
                .selectedLanguage
                .collectAsStateWithLifecycle()

            CompositionLocalProvider(
                LocalLayoutDirection provides LayoutDirection.Ltr
            ) {
                VideoDownloaderTheme {
                    AppNavigation()
                }
            }
        }
    }
}