package com.app.videodownloader.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.entryProvider
import com.app.videodownloader.presentation.screens.appLanguage.screen.AppLanguageRootScreen
import com.app.videodownloader.presentation.screens.splash.screen.SplashScreen

@Composable
fun AppNavigation() {

    val backStack = rememberNavBackStack(Screen.SplashScreen)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {

            entry<Screen.SplashScreen> {
                SplashScreen(
                    backStack = backStack
                )
            }
            entry<Screen.AppLanguageScreen> {
                AppLanguageRootScreen(
                    backStack = backStack
                )
            }
        }
    )
}