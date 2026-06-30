package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.more.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdPlacementConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.nativeAd.NativeAdHost
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.more.componants.DrawerItem
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.more.events.MoreUiEvent
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.more.viewModel.MoreViewModel
import com.google.android.gms.ads.nativead.NativeAd
import org.koin.compose.viewmodel.koinViewModel
import android.content.Intent
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdPosition

@Composable
fun MoreScreen(
    onNotificationClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onHowToDownloadClicked: () -> Unit,
    onRateUsClick: () -> Unit,
    onAppLanguageClicked: () -> Unit,
    onPremiumCardClick: () -> Unit,
    onPrivacyCardClick: () -> Unit,
    viewModel: MoreViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val placementKey = NativeAdConfig.MORE

    val canShowAds = !state.isPremiumUser

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

    LaunchedEffect(placementConfig) {
        viewModel.onEvent(MoreUiEvent.ScreenStarted)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            if (placementConfig?.position == NativeAdPosition.Top) {
                MoreNativeAdItem(
                    nativeAd = nativeAd,
                    nativeAdConfig = state.nativeAdConfig,
                    placementConfig = placementConfig,
                    placementKey = placementKey
                )
            }
        },
        bottomBar = {
            if (placementConfig?.position == NativeAdPosition.Bottom) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MoreNativeAdItem(
                        nativeAd = nativeAd,
                        nativeAdConfig = state.nativeAdConfig,
                        placementConfig = placementConfig,
                        placementKey = placementKey
                    )
                    Spacer(
                        modifier = Modifier.height(15.dp)
                    )
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {


            // Premium Card
            if (!state.isPremiumUser) {
                if (state.isPremiumvisible) {
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
                                text = stringResource(R.string.premium_plan),
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.W800
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = stringResource(R.string.premium_plan_description),
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W400
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Box(
                                modifier = Modifier
                                    .background(Color.Yellow, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                                    .clickable {
                                        onPremiumCardClick()
                                    }
                            ) {
                                Text(
                                    text = stringResource(R.string.upgrade_now),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
            Text(
                text = stringResource(R.string.general_settings),
                fontSize = 10.sp,
                fontWeight = FontWeight.W800,
                color = Color(0xFF8B95A5)
            )

            DrawerItem(
                title = stringResource(R.string.languages),
                description = stringResource(R.string.choose_your_preferred_language),
                icon = R.drawable.ic_world,
                onClick = {
                    onAppLanguageClicked()
                }
            )

            DrawerItem(
                title = stringResource(R.string.how_to_download),
                description = stringResource(R.string.step_by_step_guide),
                icon = R.drawable.ic_how,
                onClick = {
                    onHowToDownloadClicked()
                }
            )

            DrawerItem(
                title = stringResource(R.string.notifications),
                description = stringResource(R.string.set_up_your_alerts),
                icon = R.drawable.ic_bell,
                onClick = onNotificationClick
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.support_and_share),
                fontSize = 10.sp,
                fontWeight = FontWeight.W800,
                color = Color(0xFF8B95A5)
            )

            DrawerItem(
                title = stringResource(R.string.share_app),
                description = stringResource(R.string.invite_friends),
                icon = R.drawable.ic_share,
                onClick = {
                    context.shareApp()
                }
            )

            DrawerItem(
                title = stringResource(R.string.rate_us),
                description = stringResource(R.string.give_feedback_on_store),
                icon = R.drawable.ic_star,
                onClick = {
                    onRateUsClick()
                }
            )

            DrawerItem(
                title = stringResource(R.string.feedback),
                description = stringResource(R.string.send_us_your_suggestions),
                icon = R.drawable.ic_mail,
                onClick = {
                    onFeedbackClick()
                }
            )

            if (state.isPrivacyPolicyVisible) {
                DrawerItem(
                    title = stringResource(R.string.privacy_policy),
                    description = stringResource(R.string.legal_and_usage_terms),
                    icon = R.drawable.ic_description,
                    onClick = {
                        onPrivacyCardClick()
                    }
                )
            }


        }
    }
}

@Composable
private fun MoreNativeAdItem(
    nativeAd: NativeAd?,
    nativeAdConfig: NativeAdConfig,
    placementConfig: NativeAdPlacementConfig?,
    placementKey: String,
) {
    if (placementConfig == null) return

    NativeAdHost(
        nativeAd = nativeAd,
        nativeAdConfig = nativeAdConfig,
        placementConfig = placementConfig,
        placementKey = placementKey
    )
}

private fun android.content.Context.shareApp() {
    val appName = getString(R.string.app_name)
    val packageName = packageName
    val playStoreUrl = "https://play.google.com/store/apps/details?id=$packageName"

    val shareMessage = getString(
        R.string.share_app_message,
        appName,
        playStoreUrl
    )

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, appName)
        putExtra(Intent.EXTRA_TEXT, shareMessage)
    }

    val chooserIntent = Intent.createChooser(
        shareIntent,
        getString(R.string.share_app)
    )

    runCatching {
        startActivity(chooserIntent)
    }.onFailure {
        Toast.makeText(
            this,
            getString(R.string.no_app_found_to_share),
            Toast.LENGTH_SHORT
        ).show()
    }
}

