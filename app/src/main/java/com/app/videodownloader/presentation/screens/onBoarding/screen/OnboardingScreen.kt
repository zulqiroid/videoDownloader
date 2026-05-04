package com.app.videodownloader.presentation.screens.onBoarding.screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.app.videodownloader.domain.model.ads.BannerAdScreen
import com.app.videodownloader.domain.model.ads.BannerAdSlot
import com.app.videodownloader.domain.model.ads.NativeAdPosition
import com.app.videodownloader.presentation.ads.banner.componants.BannerAdHost
import com.app.videodownloader.presentation.ads.banner.viewModel.BannerAdViewModel
import com.app.videodownloader.presentation.ads.nativeAd.NativeAdHost
import com.app.videodownloader.presentation.componants.AppButton
import com.app.videodownloader.presentation.componants.exitConfirmationDialogue.ExitConfirmationDialog
import com.app.videodownloader.presentation.componants.privacyPolicyDialgue.PrivacyDialogHost
import com.app.videodownloader.presentation.navigation.Screen
import com.app.videodownloader.presentation.screens.appLanguage.events.AppLanguageUiEvents
import com.app.videodownloader.presentation.screens.onBoarding.componants.ActionButton
import com.app.videodownloader.presentation.screens.onBoarding.componants.OnboardingPage
import com.app.videodownloader.presentation.screens.onBoarding.componants.SmoothElongateIndicator
import com.app.videodownloader.presentation.screens.onBoarding.events.OnboardingEvents
import com.app.videodownloader.presentation.screens.onBoarding.events.OnboardingNavEvent
import com.app.videodownloader.presentation.screens.onBoarding.viewModel.OnboardingViewModel
import org.koin.compose.viewmodel.koinViewModel

// OnboardingScreen.kt
@Composable
fun OnboardingScreen(
    backStack: NavBackStack<NavKey>,
    viewModel: OnboardingViewModel = koinViewModel(),
    bannerAdViewModel: BannerAdViewModel = koinViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { state.pages.size })
    val bannerState by bannerAdViewModel.state.collectAsState()

    val bannerScreen = BannerAdScreen.OnBoarding

    val showTopBanner = bannerState.config.isEnabled(
        screen = bannerScreen,
        slot = BannerAdSlot.Top
    )

    val showBottomBanner = bannerState.config.isEnabled(
        screen = bannerScreen,
        slot = BannerAdSlot.Bottom
    )

    BackHandler {
        viewModel.onEvent(OnboardingEvents.OnBackClicked)
    }

    // Effects
    LaunchedEffect(Unit) {
        viewModel.onEvent(OnboardingEvents.ScreenStarted)

        viewModel.navEvents.collect {
            when (it) {
                OnboardingNavEvent.NavigateToHome -> {
                    backStack.clear()
                    backStack.add(Screen.Main)
                    backStack.add(Screen.Premium)
                }

                OnboardingNavEvent.ExitApp -> {
                    backStack.clear()
                }
            }
        }
    }

    // Sync pager → state
    LaunchedEffect(state.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onEvent(OnboardingEvents.PageChanged(pagerState.currentPage))
    }


    val currentModel = state.pages.getOrNull(state.currentPage)
    val currentPlacementKey = currentModel?.nativeAdPlacementKey
    val currentPlacementConfig = currentPlacementKey
        ?.let { key -> state.nativeAdConfig.placement(key) }

    val currentNativeAd = currentPlacementKey
        ?.let { key -> state.nativeAds[key] }

    val shouldShowBottomNativeAd =
        currentPlacementConfig?.position == NativeAdPosition.Bottom

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier.padding(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showTopBanner){
                    BannerAdHost(
                        config = bannerState.config,
                        screen = bannerScreen,
                        slot = BannerAdSlot.Top
                    )
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier.padding(
                    top = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showBottomBanner){
                    BannerAdHost(
                        config = bannerState.config,
                        screen = bannerScreen,
                        slot = BannerAdSlot.Bottom
                    )
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                ) {

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) { page ->
                        val model = state.pages[page]
                        val placementKey = model.nativeAdPlacementKey

                        OnboardingPage(
                            model = model,
                            nativeAd = if (page == state.currentPage && placementKey != null) {
                                state.nativeAds[placementKey]
                            } else {
                                null
                            },
                            nativeAdConfig = state.nativeAdConfig
                        )
                    }

                    AnimatedVisibility(
                        visible = !state.isLastPage
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 25.dp, vertical = 15.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SmoothElongateIndicator(
                                    pageCount = state.pages.size,
                                    currentPage = state.currentPage,
                                    onPageSelected = {
                                        viewModel.onEvent(OnboardingEvents.PageChanged(it))
                                    }
                                )

                                AppButton(
                                    padding = 0,
                                    modifier = Modifier,
                                    text = if (state.isLastPage) "Continue" else "Next",
                                    onClick = {
                                        if (state.isLastPage) {
                                            viewModel.onEvent(OnboardingEvents.ContinueClicked)
                                        } else {
                                            viewModel.onEvent(OnboardingEvents.NextClicked)
                                        }
                                    }
                                )
                            }

                            if (
                                shouldShowBottomNativeAd &&
                                currentPlacementConfig != null
                            ) {


                                NativeAdHost(
                                    nativeAd = currentNativeAd,
                                    nativeAdConfig = state.nativeAdConfig,
                                    placementConfig = currentPlacementConfig
                                )


                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = state.isLastPage
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent)
                                .padding(20.dp)
                                .border(
                                    color = Color(0xFFE00004),
                                    width = 1.5.dp,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            // Title Row (Video + Downloader)
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Video ",
                                    fontSize = 33.sp,
                                    fontWeight = FontWeight.W800,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Downloader",
                                    fontSize = 33.sp,
                                    fontWeight = FontWeight.W800,
                                    color = Color(0xFFE00004)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Subtitle
                            Text(
                                text = "The advanced download engine.",
                                fontSize = 18.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.W400,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            AppButton(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                text = "Continue",
                                onClick = {
                                    viewModel.onEvent(OnboardingEvents.ContinueClicked)
                                }
                            )
                        }
                    }

                }
            }

            ExitConfirmationDialog(
                visible = state.showExitDialogue,
                onExitClick = {
                    viewModel.onEvent(OnboardingEvents.OnDialogueExitClicked)
                },
                onCancelClick = {
                    viewModel.onEvent(OnboardingEvents.OnDialogueCancelCLicked)
                }
            )

            PrivacyDialogHost(
                isShowDialogue = state.showPolicyDialogue,
                onAgree = {
                    viewModel.onEvent(OnboardingEvents.OnPolicyDialogueAcceptClicked)
                }
            )
        }
    }
}