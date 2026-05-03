package com.app.videodownloader.presentation.screens.more.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.videodownloader.R
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.model.ads.NativeAdPlacementConfig
import com.app.videodownloader.presentation.ads.nativeAd.NativeAdHost
import com.app.videodownloader.presentation.screens.more.componants.DrawerItem
import com.app.videodownloader.presentation.screens.more.events.MoreUiEvent
import com.app.videodownloader.presentation.screens.more.viewModel.MoreViewModel
import com.google.android.gms.ads.nativead.NativeAd
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MoreScreen(
    onNotificationClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onHowToDownloadClicked: () -> Unit,
    onRateUsClick: () -> Unit,
    viewModel: MoreViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    val topPlacementKey = NativeAdConfig.MORE_TOP
    val bottomPlacementKey = NativeAdConfig.MORE_BOTTOM

    val topPlacementConfig = state.nativeAdConfig.placement(topPlacementKey)
    val bottomPlacementConfig = state.nativeAdConfig.placement(bottomPlacementKey)

    val topNativeAd = state.nativeAds[topPlacementKey]
    val bottomNativeAd = state.nativeAds[bottomPlacementKey]

    LaunchedEffect(topPlacementConfig, bottomPlacementConfig) {
        viewModel.onEvent(MoreUiEvent.ScreenStarted)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            MoreNativeAdItem(
                nativeAd = topNativeAd,
                nativeAdConfig = state.nativeAdConfig,
                placementConfig = topPlacementConfig,
                placementKey = topPlacementKey
            )

            // Premium Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(20.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFE00004),
                                    Color(0xFF7A0002),
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clip(RoundedCornerShape(24.dp))
                        .padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(R.drawable.big_download_arrow),
                            contentDescription = "big download arrow",
                            modifier = Modifier
                                .weight(1f)
                                .alpha(0.2f)
                        )

                        Image(
                            painter = painterResource(R.drawable.premiun_diamong_img),
                            contentDescription = "premium diamond icon",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "PREMIUM PLAN",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.W800
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Enjoy ad-free experience and \n faster download speeds.",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .background(Color.Yellow, RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text("Upgrade Now", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "GENERAL SETTINGS",
                fontSize = 10.sp,
                fontWeight = FontWeight.W800,
                color = Color(0xFF8B95A5)
            )

            DrawerItem(
                title = "Languages",
                description = "Choose your preferred language",
                icon = R.drawable.ic_world,
                onClick = {
                    // TODO
                }
            )

            DrawerItem(
                title = "How to Download",
                description = "Step-by-step guide",
                icon = R.drawable.ic_how,
                onClick = {
                    onHowToDownloadClicked()
                }
            )

            DrawerItem(
                title = "Notifications",
                description = "Set up your alerts",
                icon = R.drawable.ic_bell,
                onClick = onNotificationClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "SUPPORT & SHARE",
                fontSize = 10.sp,
                fontWeight = FontWeight.W800,
                color = Color(0xFF8B95A5)
            )

            DrawerItem(
                title = "Share App",
                description = "Invite friends",
                icon = R.drawable.ic_share,
                onClick = {
                    // TODO
                }
            )

            DrawerItem(
                title = "Rate Us",
                description = "Give feedback on the store",
                icon = R.drawable.ic_star,
                onClick = {
                    onRateUsClick()
                }
            )

            DrawerItem(
                title = "Feedback",
                description = "Send us your suggestions",
                icon = R.drawable.ic_mail,
                onClick = {
                    onFeedbackClick()
                }
            )

            DrawerItem(
                title = "Privacy Policy",
                description = "Legal and usage terms",
                icon = R.drawable.ic_description,
                onClick = {
                    // TODO
                }
            )

            MoreNativeAdItem(
                nativeAd = bottomNativeAd,
                nativeAdConfig = state.nativeAdConfig,
                placementConfig = bottomPlacementConfig,
                placementKey = bottomPlacementKey
            )
        }
    }
}

@Composable
private fun MoreNativeAdItem(
    nativeAd: NativeAd?,
    nativeAdConfig: NativeAdConfig,
    placementConfig: NativeAdPlacementConfig?,
    placementKey: String
) {
    if (placementConfig == null) return

    NativeAdHost(
        nativeAd = nativeAd,
        nativeAdConfig = nativeAdConfig,
        placementConfig = placementConfig,
        placementKey = placementKey
    )
}