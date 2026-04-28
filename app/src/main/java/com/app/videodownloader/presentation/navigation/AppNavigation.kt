package com.app.videodownloader.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.entryProvider
import com.app.videodownloader.presentation.screens.appLanguage.screen.AppLanguageRootScreen
import com.app.videodownloader.presentation.screens.main.screen.MainScreen
import com.app.videodownloader.presentation.screens.onBoarding.screen.OnboardingScreen
import com.app.videodownloader.presentation.screens.splash.screen.SplashScreen

@Composable
fun AppNavigation() {

    val backStack = rememberNavBackStack(Screen.Splash)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {

            entry<Screen.Splash> {
                SplashScreen(
                    backStack = backStack
                )
            }
            entry<Screen.AppLanguage> {
                AppLanguageRootScreen(
                    backStack = backStack
                )
            }
            entry<Screen.OnBoarding> {
                OnboardingScreen(
                    backStack = backStack
                )
            }
            entry<Screen.Main> {
                MainScreen(
                    backStack = backStack
                )
            }
        }
    )
}