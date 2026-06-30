package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.splash.screen

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.FromWhichSrc
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdPosition
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.nativeAd.NativeAdHost
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.componants.AppButton
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.componants.exitConfirmationDialogue.ExitConfirmationDialog
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.navigation.Screen
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.splash.events.SplashNavEvents
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.splash.events.SplashUiEvents
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.splash.viewModel.SplashViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashScreen(
    backStack: NavBackStack<NavKey>,
    viewModel: SplashViewModel = koinViewModel(),
) {
    val state by viewModel.states.collectAsStateWithLifecycle()
    val activity = LocalActivity.current

    val splashPlacementConfig = state.nativeAdConfig.placement(NativeAdConfig.SPLASH)
    val splashNativeAd = state.nativeAds[NativeAdConfig.SPLASH]

    val showSplashNativeAd =
        !state.isPremiumUser &&
                state.isConsentReady &&
                splashPlacementConfig != null &&
                splashNativeAd != null

    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collect { event ->
            when (event) {
                SplashNavEvents.NavigateToLanguageSRC -> {
                    backStack.clear()
                    backStack.add(Screen.AppLanguage(FromWhichSrc.FROM_SPLASH))
                }

                SplashNavEvents.ExitApp -> {
                    backStack.clear()
                }

                SplashNavEvents.NavigateToMainSrc -> {
                    backStack.clear()
                    backStack.add(Screen.Main)
                }
            }
        }
    }

    LaunchedEffect(activity) {
        viewModel.onEvent(
            SplashUiEvents.OnSplashStarted(activity)
        )
    }

    BackHandler {
        viewModel.onEvent(SplashUiEvents.OnBackClicked)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(R.drawable.splash_bg),
                contentDescription = "splash background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (
                    showSplashNativeAd &&
                    splashPlacementConfig.position == NativeAdPosition.Top
                ) {
                    NativeAdHost(
                        nativeAd = splashNativeAd,
                        nativeAdConfig = state.nativeAdConfig,
                        placementConfig = splashPlacementConfig,
                        placementKey = NativeAdConfig.SPLASH,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (
                    showSplashNativeAd &&
                    splashPlacementConfig.position == NativeAdPosition.FullPage
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        NativeAdHost(
                            nativeAd = splashNativeAd,
                            nativeAdConfig = state.nativeAdConfig,
                            placementConfig = splashPlacementConfig,
                            placementKey = NativeAdConfig.SPLASH,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.app_icon_main),
                            contentDescription = "app icon on splash",
                            modifier = Modifier
                                .size(128.dp)
                                .clip(RoundedCornerShape(32.dp))
                        )

                        Spacer(modifier = Modifier.size(18.dp))

                        Text(
                            text = stringResource(R.string.app_name),
                            fontSize = 36.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.size(12.dp))

                        Text(
                            text = stringResource(R.string.splash_download_videos_instantly),
                            fontSize = 18.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                if (
                    showSplashNativeAd &&
                    splashPlacementConfig?.position == NativeAdPosition.Bottom &&
                    splashNativeAd != null
                ) {
                    NativeAdHost(
                        nativeAd = splashNativeAd,
                        nativeAdConfig = state.nativeAdConfig,
                        placementConfig = splashPlacementConfig,
                        placementKey = NativeAdConfig.SPLASH,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column {
                    AppButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = when {
                            state.isStarting -> stringResource(R.string.please_wait)
                            !state.isConsentReady -> stringResource(R.string.preparing)
                            else -> stringResource(R.string.get_started)
                        },
                        onClick = {
                            if (!state.isStarting && state.isConsentReady) {
                                viewModel.onEvent(
                                    SplashUiEvents.OnGetStartedClicked(activity)
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.size(10.dp))

                    Text(
                        text = stringResource(R.string.app_may_contain_ads),
                        fontSize = 12.sp,
                        color = Color(0xFFC7C6C6),
                        fontWeight = FontWeight.W400,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            ExitConfirmationDialog(
                visible = state.showExitDialogue,
                onExitClick = {
                    viewModel.onEvent(SplashUiEvents.OnDialogueExitClicked)
                },
                onCancelClick = {
                    viewModel.onEvent(SplashUiEvents.OnDialogueCancelCLicked)
                }
            )
        }
    }
}