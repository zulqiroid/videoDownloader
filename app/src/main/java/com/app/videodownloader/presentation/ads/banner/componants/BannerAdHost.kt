package com.app.videodownloader.presentation.ads.banner.componants

import android.os.Bundle
import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.videodownloader.domain.model.ads.AdState
import com.app.videodownloader.domain.model.ads.BannerAdConfig
import com.app.videodownloader.domain.model.ads.BannerAdScreen
import com.app.videodownloader.domain.model.ads.BannerAdSlot
import com.app.videodownloader.domain.usecases.ads.CanRequestAdsUseCase
import com.app.videodownloader.domain.usecases.billing.ObserveIsPremiumUserUseCase
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import org.koin.compose.getKoin
 import org.koin.compose.getKoin

@Composable
fun BannerAdHost(
    config: BannerAdConfig,
    screen: BannerAdScreen,
    slot: BannerAdSlot,
    modifier: Modifier = Modifier,
    onAdStateChanged: (AdState) -> Unit = {}
) {

    if (!config.isEnabled(screen, slot)) {
        return
    }

    val observeIsPremiumUserUseCase: ObserveIsPremiumUserUseCase = getKoin().get()
    val isPremiumUser by observeIsPremiumUserUseCase().collectAsStateWithLifecycle(
        initialValue = false
    )

    if (isPremiumUser) {
        onAdStateChanged(
            AdState.Skipped("Banner skipped: premium user")
        )
        return
    }



    val canRequestAdsUseCase: CanRequestAdsUseCase = getKoin().get()

    if (!canRequestAdsUseCase()) {
        onAdStateChanged(
            AdState.Skipped("Banner ad skipped: consent not ready")
        )
        return
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(BANNER_CONTAINER_HEIGHT_DP.dp)
    ) {
        val context = LocalContext.current
        val adWidthDp = maxWidth.value
            .toInt()
            .coerceAtLeast(MIN_BANNER_WIDTH_DP)

        key(
            config.adUnitId,
            screen,
            slot,
            adWidthDp,
            config.collapsibleEnabled,
            config.collapsiblePosition
        ) {
            var showPlaceholder by remember { mutableStateOf(true) }

            val adView = remember {
                AdView(context).apply {
                    setAdUnitId(config.adUnitId)

                    setAdSize(
                        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                            context,
                            adWidthDp
                        )
                    )

                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            Log.d(TAG, "Banner loaded. screen=$screen slot=$slot")
                            showPlaceholder = false
                            onAdStateChanged(AdState.Loaded)
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            Log.e(
                                TAG,
                                "Banner failed. screen=$screen slot=$slot code=${error.code}, message=${error.message}"
                            )

                            /*
                             * Hide placeholder after failure so the screen does not keep showing
                             * a fake loading ad forever.
                             */
                            showPlaceholder = false

                            onAdStateChanged(
                                AdState.LoadFailed(
                                    errorCode = error.code,
                                    errorMessage = error.message
                                )
                            )
                        }

                        override fun onAdOpened() {
                            Log.d(TAG, "Banner opened. screen=$screen slot=$slot")
                            onAdStateChanged(AdState.Showing)
                        }

                        override fun onAdClosed() {
                            Log.d(TAG, "Banner closed. screen=$screen slot=$slot")
                            onAdStateChanged(AdState.Dismissed)
                        }

                        override fun onAdClicked() {
                            Log.d(TAG, "Banner clicked. screen=$screen slot=$slot")
                            onAdStateChanged(AdState.Clicked)
                        }

                        override fun onAdImpression() {
                            Log.d(TAG, "Banner impression. screen=$screen slot=$slot")
                            onAdStateChanged(AdState.Impression)
                        }
                    }
                }
            }

            LaunchedEffect(adView) {
                showPlaceholder = true
                onAdStateChanged(AdState.Loading)

                val requestBuilder = AdRequest.Builder()

                if (config.collapsibleEnabled) {
                    requestBuilder.addNetworkExtrasBundle(
                        AdMobAdapter::class.java,
                        Bundle().apply {
                            putString("collapsible", config.collapsiblePosition)
                        }
                    )
                }

                adView.loadAd(requestBuilder.build())
            }

            DisposableEffect(adView) {
                onDispose {
                    Log.d(TAG, "Destroying banner. screen=$screen slot=$slot")

                    adView.adListener = object : AdListener() {}
                    adView.pause()
                    adView.destroy()
                }
            }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                if (showPlaceholder) {
                    BannerAdLoadingPlaceholder(
                        modifier = Modifier.matchParentSize()
                    )
                }

                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(BANNER_CONTAINER_HEIGHT_DP.dp),
                    factory = {
                        adView
                    },
                    update = { view ->
                        view.resume()
                    }
                )
            }
        }
    }
}

@Composable
private fun BannerAdLoadingPlaceholder(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "banner_ad_placeholder")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "banner_ad_placeholder_alpha"
    )

    Box(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .background(
                color = Color(0xFFE5E7EB).copy(alpha = alpha),
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Loading ad",
            color = Color(0xFF94A3B8)
        )
    }
}

private const val TAG = "BannerAdHost"
private const val MIN_BANNER_WIDTH_DP = 320
private const val BANNER_CONTAINER_HEIGHT_DP = 70