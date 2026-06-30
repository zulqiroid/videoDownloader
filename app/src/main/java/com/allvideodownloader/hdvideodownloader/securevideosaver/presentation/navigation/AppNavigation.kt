package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.navigation

        import androidx.compose.runtime.Composable
        import androidx.navigation3.runtime.rememberNavBackStack
        import androidx.navigation3.ui.NavDisplay
        import androidx.navigation3.runtime.entryProvider
        import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.appLanguage.screen.AppLanguageRootScreen
        import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.downloadGuide.screen.DownloadGuideRoute
        import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.screen.MainScreen
        import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.screen.MediaPlayerScreen
        import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.onBoarding.screen.OnboardingScreen
        import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.premium.screen.PremiumRoute
        import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.splash.screen.SplashScreen

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
                    entry<Screen.AppLanguage> { params ->
                        AppLanguageRootScreen(
                            fromWhichScreen = params.src,
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
                    entry<Screen.MediaPlayer> { params ->
                        MediaPlayerScreen(
                            mediaList = params.mediaList,
                            startIndex = params.startIndex,
                             backStack = backStack
                         )
                    }
                    entry<Screen.DownloadGuide> { params ->
                        DownloadGuideRoute(
                            backStack = backStack
                        )
                    }
                    entry<Screen.Premium> { params ->
                        PremiumRoute(
                            backStack = backStack
                        )
                    }

                }
            )
        }