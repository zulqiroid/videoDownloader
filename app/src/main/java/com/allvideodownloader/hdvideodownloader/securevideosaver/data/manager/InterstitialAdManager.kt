package com.allvideodownloader.hdvideodownloader.securevideosaver.data.manager

import android.app.Activity
import android.content.Context
import android.util.Log
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdsScreens
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdsScreens.*
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.InterstitialAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.InterstitialAdPlacement
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.billing.PremiumAccessController
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.CanRequestAdsUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveInterstitialAdConfigUseCase
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.pow

class InterstitialAdManager(
    context: Context,
    observeInterstitialAdConfigUseCase: ObserveInterstitialAdConfigUseCase,
    private val canRequestAdsUseCase: CanRequestAdsUseCase,
    private val fullScreenAdCoordinator: FullScreenAdCoordinator,
    private val premiumAccessController: PremiumAccessController,
) {

    private val appContext = context.applicationContext

    private val managerScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )

    @Volatile
    private var config: InterstitialAdConfig = InterstitialAdConfig.default()

    @Volatile
    private var cachedAd: InterstitialAd? = null

    @Volatile
    private var isLoadingAd: Boolean = false

    @Volatile
    private var isShowingAd: Boolean = false

    @Volatile
    private var adLoadedAtMs: Long = 0L

    @Volatile
    private var lastShownAtMs: Long = 0L

    private var loadRetryAttempt: Int = 0

    private val triggerCounts = mutableMapOf<InterstitialAdPlacement, Int>()

    init {
        managerScope.launch {
            observeInterstitialAdConfigUseCase().collect { newConfig ->
                val oldConfig = config
                config = newConfig

                Log.d(TAG, "Interstitial config updated: $newConfig")

                if (!newConfig.enabled) {
                    clearCurrentAd()
                    return@collect
                }

                if (oldConfig.adUnitId != newConfig.adUnitId) {
                    clearCurrentAd()
                }

                if (!canRequestAdsUseCase()) {
                    Log.d(TAG, "Interstitial auto-load skipped: consent not ready.")
                    return@collect
                }

                if (premiumAccessController.isPremium()) {
                    Log.d(TAG, "Interstitial auto-load skipped: premium user.")
                    clearCurrentAd()
                    return@collect
                }

                if (!isAdReady() && !isLoadingAd) {
                    loadAd()
                }
            }
        }
    }

    fun getCurrentConfig(): InterstitialAdConfig {
        return config
    }

    fun isAdReady(): Boolean {
        return cachedAd != null && !isAdExpired()
    }

    fun loadAd(
        AdScreen: AdsScreens = AdsScreens.Default,
        onStateChanged: (AdState) -> Unit = {}
    ) {
        val currentConfig = config

        if (premiumAccessController.isPremium()) {
            onStateChanged(AdState.Skipped("Interstitial load skipped: premium user"))
            clearCurrentAd()
            return
        }

        if (!canRequestAdsUseCase()) {
            onStateChanged(AdState.Skipped("Interstitial load skipped: consent not ready"))
            return
        }

        if (!currentConfig.enabled) {
            onStateChanged(AdState.Skipped("Interstitial ads are disabled by remote config"))
            return
        }

        if (isLoadingAd) {
            Log.d(TAG, "Interstitial load skipped: load already in progress.")
            return
        }

        if (isAdReady()) {
            Log.d(TAG, "Interstitial load skipped: valid cached ad already exists.")
            onStateChanged(AdState.Loaded)
            return
        }

        startLoading(
            AdScreen =  AdScreen,
            config = currentConfig,
            onStateChanged = onStateChanged
        )
    }

    fun showAdIfAvailable(
        activity: Activity,
        placement: InterstitialAdPlacement = InterstitialAdPlacement.Generic,
        forceShow: Boolean = false,
        onStateChanged: (AdState) -> Unit = {},
        onComplete: () -> Unit = {}
    ) {
        val currentConfig = config

        if (premiumAccessController.isPremium()) {
            completeSkipped(
                reason = "Interstitial show skipped: premium user",
                onStateChanged = onStateChanged,
                onComplete = onComplete
            )
            return
        }

        if (!canRequestAdsUseCase()) {
            completeSkipped(
                reason = "Interstitial show skipped: consent not ready",
                onStateChanged = onStateChanged,
                onComplete = onComplete
            )
            return
        }

        if (!currentConfig.enabled) {
            completeSkipped(
                reason = "Interstitial ads are disabled by remote config",
                onStateChanged = onStateChanged,
                onComplete = onComplete
            )
            return
        }

        if (!currentConfig.isPlacementEnabled(placement)) {
            completeSkipped(
                reason = "Interstitial placement disabled: $placement",
                onStateChanged = onStateChanged,
                onComplete = onComplete
            )
            return
        }

        if (activity.isFinishing || activity.isDestroyed) {
            completeSkipped(
                reason = "Activity is not valid for showing interstitial",
                onStateChanged = onStateChanged,
                onComplete = onComplete
            )
            return
        }

        if (isShowingAd) {
            completeSkipped(
                reason = "Interstitial is already showing",
                onStateChanged = onStateChanged,
                onComplete = onComplete
            )
            return
        }

        if (!forceShow && !shouldShowForTriggerCount(currentConfig, placement)) {
            loadAd()
            completeSkipped(
                reason = "Interstitial skipped by trigger count for placement: $placement",
                onStateChanged = onStateChanged,
                onComplete = onComplete
            )
            return
        }

        if (!forceShow && !canShowByFrequencyPolicy(currentConfig)) {
            loadAd()
            completeSkipped(
                reason = "Interstitial skipped by frequency cap",
                onStateChanged = onStateChanged,
                onComplete = onComplete
            )
            return
        }

        if (!isAdReady()) {
            loadAd(onStateChanged = onStateChanged)
            completeSkipped(
                reason = "No interstitial ad is ready",
                onStateChanged = onStateChanged,
                onComplete = onComplete
            )
            return
        }

        presentAd(
            activity = activity,
            onStateChanged = onStateChanged,
            onComplete = onComplete
        )
    }

    private fun startLoading(
        AdScreen: AdsScreens,
        config: InterstitialAdConfig,
        onStateChanged: (AdState) -> Unit
    ) {
        isLoadingAd = true
        onStateChanged(AdState.Loading)

        val id = when(AdScreen){
            Splash -> config.splashUnitId
            Language -> config.languageUnitId
            OnBoarding -> config.introUnitId
            Premium -> config.premiumUnitId
            else -> config.adUnitId
        }

        Log.d(TAG, "Loading Interstitial ad with unit: ${config.adUnitId}")

        InterstitialAd.load(
            appContext,
            id,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {

                override fun onAdLoaded(ad: InterstitialAd) {
                    cachedAd = ad
                    adLoadedAtMs = now()
                    isLoadingAd = false
                    loadRetryAttempt = 0

                    ad.onPaidEventListener = OnPaidEventListener { adValue ->
                        Log.d(
                            TAG,
                            "Interstitial paid event: valueMicros=${adValue.valueMicros}, currency=${adValue.currencyCode}"
                        )
                    }

                    Log.d(TAG, "Interstitial ad loaded.")
                    onStateChanged(AdState.Loaded)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    cachedAd = null
                    adLoadedAtMs = 0L
                    isLoadingAd = false

                    Log.e(
                        TAG,
                        "Interstitial failed to load. code=${error.code}, message=${error.message}, domain=${error.domain}"
                    )

                    onStateChanged(
                        AdState.LoadFailed(
                            errorCode = error.code,
                            errorMessage = error.message
                        )
                    )

                    scheduleRetryLoad()
                }
            }
        )
    }

    private fun presentAd(
        activity: Activity,
        onStateChanged: (AdState) -> Unit,
        onComplete: () -> Unit
    ) {
        val ad = cachedAd

        if (ad == null) {
            onStateChanged(AdState.ShowFailed("Cached interstitial ad was null"))
            loadAd(onStateChanged = onStateChanged)
            onComplete()
            return
        }

        var completed = false

        fun completeOnce() {
            if (!completed) {
                completed = true
                onComplete()
            }
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
                fullScreenAdCoordinator.onFullScreenAdStarted()
                lastShownAtMs = now()

                Log.d(TAG, "Interstitial ad is showing.")
                onStateChanged(AdState.Showing)
            }

            override fun onAdImpression() {
                Log.d(TAG, "Interstitial impression recorded.")
                onStateChanged(AdState.Impression)
            }

            override fun onAdClicked() {
                Log.d(TAG, "Interstitial clicked.")
                onStateChanged(AdState.Clicked)
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Interstitial dismissed.")

                fullScreenAdCoordinator.onFullScreenAdFinished()
                clearCurrentAd()
                onStateChanged(AdState.Dismissed)

                loadAd()
                completeOnce()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.e(
                    TAG,
                    "Interstitial failed to show. code=${error.code}, message=${error.message}"
                )

                fullScreenAdCoordinator.onFullScreenAdFinished()
                clearCurrentAd()

                onStateChanged(
                    AdState.ShowFailed(error.message)
                )

                loadAd()
                completeOnce()
            }
        }

        try {
            ad.show(activity)
        }catch (exception: Exception) {
            Log.e(TAG, "Interstitial show crashed.", exception)

            fullScreenAdCoordinator.onFullScreenAdFinished()
            clearCurrentAd()

            onStateChanged(
                AdState.ShowFailed(
                    exception.message ?: "Unable to show interstitial"
                )
            )

            loadAd()
            completeOnce()
        }
    }

    private fun completeSkipped(
        reason: String,
        onStateChanged: (AdState) -> Unit,
        onComplete: () -> Unit
    ) {
        Log.d(TAG, reason)
        onStateChanged(AdState.Skipped(reason))
        onComplete()
    }

    private fun shouldShowForTriggerCount(
        config: InterstitialAdConfig,
        placement: InterstitialAdPlacement
    ): Boolean {
        val requiredCount = config.triggerCountForPlacement(placement)
        val currentCount = (triggerCounts[placement] ?: 0) + 1

        triggerCounts[placement] = if (currentCount >= requiredCount) {
            0
        } else {
            currentCount
        }

        return currentCount >= requiredCount
    }

    private fun InterstitialAdConfig.isPlacementEnabled(
        placement: InterstitialAdPlacement
    ): Boolean {
        return when (placement) {
            InterstitialAdPlacement.TabSwitch -> showOnTabSwitch
            InterstitialAdPlacement.PlayMedia -> showOnPlayMedia
            InterstitialAdPlacement.DownloadClick -> showOnDownloadClick
            InterstitialAdPlacement.ReelOpen -> showOnReelOpen
            InterstitialAdPlacement.SocialOpen -> showOnSocialOpen
            InterstitialAdPlacement.BackNavigation -> showOnBackNavigation
            InterstitialAdPlacement.Premium -> showOnPremium
            InterstitialAdPlacement.Generic -> true
            InterstitialAdPlacement.OnBoarding -> showOnOnboarding
            InterstitialAdPlacement.Language -> showOnLanguage
        }
    }

    private fun InterstitialAdConfig.triggerCountForPlacement(
        placement: InterstitialAdPlacement
    ): Int {
        return when (placement) {
            InterstitialAdPlacement.TabSwitch -> tabSwitchTriggerCount
            InterstitialAdPlacement.PlayMedia -> playMediaTriggerCount
            InterstitialAdPlacement.DownloadClick -> downloadClickTriggerCount
            InterstitialAdPlacement.ReelOpen -> reelOpenTriggerCount
            InterstitialAdPlacement.SocialOpen -> socialOpenTriggerCount
            InterstitialAdPlacement.BackNavigation -> backNavigationTriggerCount
            InterstitialAdPlacement.Premium -> premiumTriggerCount
            InterstitialAdPlacement.Generic -> 1
            InterstitialAdPlacement.OnBoarding -> onboardingTriggerCount
            InterstitialAdPlacement.Language -> languageTriggerCount
        }.coerceAtLeast(1)
    }

    private fun scheduleRetryLoad() {
        val currentConfig = config

        if (loadRetryAttempt >= currentConfig.maxLoadRetryCount) {
            Log.d(TAG, "Interstitial retry skipped: max retry count reached.")
            return
        }

        loadRetryAttempt++

        val exponentialDelay = currentConfig.initialRetryDelayMs *
                2.0.pow(loadRetryAttempt - 1).toLong()

        val retryDelay = min(
            exponentialDelay,
            currentConfig.maxRetryDelayMs
        )

        Log.d(TAG, "Scheduling Interstitial retry in $retryDelay ms.")

        managerScope.launch {
            delay(retryDelay)
            loadAd()
        }
    }

    private fun clearCurrentAd() {
        cachedAd?.fullScreenContentCallback = null
        cachedAd?.onPaidEventListener = null
        cachedAd = null
        adLoadedAtMs = 0L
        isShowingAd = false
    }

    private fun isAdExpired(): Boolean {
        val currentConfig = config

        if (adLoadedAtMs == 0L) return true
        return now() - adLoadedAtMs >= currentConfig.maxAdCacheDurationMs
    }

    private fun canShowByFrequencyPolicy(
        config: InterstitialAdConfig
    ): Boolean {
        if (lastShownAtMs == 0L) return true
        return now() - lastShownAtMs >= config.minIntervalBetweenShowsMs
    }

    private fun now(): Long = System.currentTimeMillis()

    companion object {
        private const val TAG = "InterstitialAdManager"
    }
}