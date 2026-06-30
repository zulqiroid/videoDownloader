package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.downloadGuide.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.BannerAdScreen
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.BannerAdSlot
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdPosition
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.billing.ObserveIsPremiumUserUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.banner.componants.BannerAdHost
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.banner.viewModel.BannerAdViewModel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.nativeAd.NativeAdHost
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.downloadGuide.events.DownloadGuideIntent
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.downloadGuide.state.DownloadGuideState
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.downloadGuide.state.GuideStep
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadGuideScreen(
    state: DownloadGuideState,
    onIntent: (DownloadGuideIntent) -> Unit,
    bannerAdViewModel: BannerAdViewModel = koinViewModel(),
) {

    val bannerState by bannerAdViewModel.state.collectAsStateWithLifecycle()

    val canShowAds = !state.isPremiumUser

    val showTopBanner = canShowAds && bannerState.config.isEnabled(
        screen = BannerAdScreen.DownloadGuide,
        slot = BannerAdSlot.Top
    )

    val showBottomBanner = canShowAds && bannerState.config.isEnabled(
        screen = BannerAdScreen.DownloadGuide,
        slot = BannerAdSlot.Bottom
    )

    val placementKey = NativeAdConfig.DOWNLOAD_GUIDE


    val placementConfig = if (canShowAds) {
        state.nativeAdConfig.placement(placementKey)
    } else {
        null
    }

    val nativeAd = if (canShowAds) {
        state.nativeAds[placementKey]
    } else {
        null
    }

    val showTopNativeAd =
        placementConfig?.position == NativeAdPosition.Top &&
                !state.isPremiumUser &&
                nativeAd != null

    val showBottomNativeAd =
        placementConfig?.position == NativeAdPosition.Bottom &&
                !state.isPremiumUser &&
                nativeAd != null


    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
            ) {
                if (showTopBanner) {

                    BannerAdHost(
                        config = bannerState.config,
                        screen = BannerAdScreen.DownloadGuide,
                        slot = BannerAdSlot.Top,
                        modifier = Modifier
                    )
                }
                TopAppBar(
                    windowInsets = WindowInsets(0.dp),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    ),
                    title = {
                        Text(
                            text = stringResource(R.string.download_guide_title),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W800,
                            color = Color(0xFF1C2024)
                        )
                    },
                    navigationIcon = {

                        IconButton(
                            onClick = {
                                onIntent(DownloadGuideIntent.OnBackClicked)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_back),
                                tint = Color(0xFF1C2024)
                            )
                        }
                    }
                )
                if (showTopNativeAd) {
                    NativeAdHost(
                        nativeAd = nativeAd,
                        nativeAdConfig = state.nativeAdConfig,
                        placementConfig = placementConfig,
                        placementKey = placementKey
                    )
                }

            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                if (showBottomNativeAd) {
                    NativeAdHost(
                        nativeAd = nativeAd,
                        nativeAdConfig = state.nativeAdConfig,
                        placementConfig = placementConfig,
                        placementKey = placementKey
                    )
                }
                BottomCTA(
                    onClick = {
                        onIntent(DownloadGuideIntent.OnGotItClicked)
                    }
                )
                if (showBottomBanner) {
                    BannerAdHost(
                        config = bannerState.config,
                        screen = BannerAdScreen.DownloadGuide,
                        slot = BannerAdSlot.Bottom,
                        modifier = Modifier
                    )
                }
            }

        },
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = state.steps,
                key = { step -> step.titleRes }
            ) { step ->
                GuideCard(step = step)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun GuideCard(
    step: GuideStep,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7F7F7)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(65.dp)
                    .height(25.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFF4766).copy(alpha = 0.1f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = Color(0xFFE00004),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(step.icon),
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(step.titleRes),
                fontSize = 18.sp,
                fontWeight = FontWeight.W700,
                color = Color(0xFF1C2024)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(step.descriptionRes),
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                color = Color(0xFF8B95A5)
            )
        }
    }
}

@Composable
fun BottomCTA(
    onClick: () -> Unit,
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(54.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE00004)
            )
        ) {
            Text(
                text = stringResource(R.string.download_guide_got_it_button),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.W700
            )
        }
    }
}