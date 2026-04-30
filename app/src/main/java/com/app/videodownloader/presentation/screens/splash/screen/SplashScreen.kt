package com.app.videodownloader.presentation.screens.splash.screen

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.app.videodownloader.R
import com.app.videodownloader.presentation.componants.AppButton
import com.app.videodownloader.presentation.componants.exitConfirmationDialogue.ExitConfirmationDialog
import com.app.videodownloader.presentation.componants.privacyPolicyDialgue.PrivacyDialogHost
import com.app.videodownloader.presentation.navigation.Screen
import com.app.videodownloader.presentation.screens.onBoarding.events.OnboardingEvents
import com.app.videodownloader.presentation.screens.splash.events.SplashNavEvents
import com.app.videodownloader.presentation.screens.splash.events.SplashUiEvents
import com.app.videodownloader.presentation.screens.splash.viewModel.SplashViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashScreen(
    backStack: NavBackStack<NavKey>,
    viewModel: SplashViewModel = koinViewModel(),
) {
    val state by viewModel.states.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collect {
            when (it) {
                SplashNavEvents.NavigateToLanguageSRC -> {
                    backStack.clear()
                    backStack.add(Screen.AppLanguage)
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
    val activity = LocalActivity.current


    BackHandler {
        viewModel.onEvent(SplashUiEvents.OnBackClicked)
    }


    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(R.drawable.splash_bg),
                contentDescription = "splash background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.app_icon),
                        contentDescription = "app icon on splash",
                        modifier = Modifier.size(128.dp)
                    )
                    Spacer(
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Video Downloader",
                        fontSize = 36.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Download videos instantly from anywhere.",
                        fontSize = 18.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal
                    )
                }

                Column(

                ) {
                    AppButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = "Get Started",
                        onClick = {
                            viewModel.onEvent(SplashUiEvents.OnGetStartedClicked(activity))
                        }
                    )
                    Spacer(
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = "This app may contains ads",
                        fontSize = 12.sp,
                        color = Color(0xFFC7C6C6),
                        fontWeight = FontWeight.W400,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
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