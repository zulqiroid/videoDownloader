package com.app.videodownloader.presentation.screens.onBoarding.screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { state.pages.size })

    BackHandler {
        viewModel.onEvent(OnboardingEvents.OnBackClicked)
    }

    // Effects
    LaunchedEffect(Unit) {
        viewModel.navEvents.collect {
            when (it) {
                OnboardingNavEvent.NavigateToHome -> {
                    backStack.clear()
                    backStack.add(Screen.Main)
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

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
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
                        Log.d("onBoardingPage", "the horizontal page is $page")
                        OnboardingPage(
                            model = state.pages[page]
                        )
                    }

                    /*    AnimatedVisibility(
                            visible = !state.isLastPage
                        ) {*/
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
                            },
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
                    // }

                    /*       AnimatedVisibility(
                               visible = state.isLastPage
                           ) {
                               Column(
                                   modifier = Modifier
                                       .fillMaxWidth()
                                       .background(Color.Transparent)
                                       .padding(20.dp),
                                   horizontalAlignment = Alignment.CenterHorizontally
                               ) {

                                   // Title Row (Video + Downloader)
                                   Row(
                                       verticalAlignment = Alignment.CenterVertically
                                   ) {
                                       Text(
                                           text = "Video ",
                                           fontSize = 36.sp,
                                           fontWeight = FontWeight.W800,
                                           color = Color(0xFF1C1C1C)
                                       )
                                       Text(
                                           text = "Downloader",
                                           fontSize = 36.sp,
                                           fontWeight = FontWeight.W800,
                                           color = Color.Red
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
                           }*/
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