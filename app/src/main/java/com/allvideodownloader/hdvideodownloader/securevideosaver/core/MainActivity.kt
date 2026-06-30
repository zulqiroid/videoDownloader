package com.allvideodownloader.hdvideodownloader.securevideosaver.core

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.localization.AppLocaleController
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.localization.BlockingLocaleReader
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.localization.LocaleContextWrapper
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.navigation.AppNavigation
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ui.theme.VideoDownloaderTheme
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.android.inject
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.PictureInPictureStateStore

class MainActivity : ComponentActivity() {

    private val appLocaleController: AppLocaleController by inject()
    private val pictureInPictureStateStore: PictureInPictureStateStore by inject()

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

        pictureInPictureStateStore.setSupported(
            supported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                    packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        )

        enableEdgeToEdge()

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                Log.d("FCM_TOKEN", token)
            }
            .addOnFailureListener { error ->
                Log.e("FCM_TOKEN", "Failed to get token", error)
            }

        setContent {

            val view = LocalView.current

            DisposableEffect(Unit) {
                val window = this@MainActivity.window

                window.navigationBarColor = Color.WHITE

                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightNavigationBars = true
                    isAppearanceLightStatusBars = true
                }

                onDispose { }
            }


            CompositionLocalProvider(
                LocalLayoutDirection provides LayoutDirection.Ltr
            ) {
                VideoDownloaderTheme {
                    AppNavigation()
                }
            }
        }
    }
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(
            isInPictureInPictureMode,
            newConfig
        )

        pictureInPictureStateStore.setInPictureInPictureMode(
            inPictureInPictureMode = isInPictureInPictureMode
        )
    }
}