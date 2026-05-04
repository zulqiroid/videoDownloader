package com.app.videodownloader.data.manager

import android.app.Activity
import android.content.Context
import android.util.Log
import com.app.videodownloader.domain.model.ads.AdState
import com.app.videodownloader.domain.model.ads.AppOpenAdConfig
import com.app.videodownloader.domain.usecases.ads.CanRequestAdsUseCase
import com.app.videodownloader.domain.usecases.ads.ObserveAppOpenAdConfigUseCase
 import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.pow

class AppOpenAdManager(
    context: Context,
    observeAppOpenAdConfigUseCase: ObserveAppOpenAdConfigUseCase,
    private val canRequestAdsUseCase: CanRequestAdsUseCase
) {

    private val appContext = context.applicationContext

    private val managerScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )

    @Volatile
    private var config: AppOpenAdConfig = AppOpenAdConfig.default()

    @Volatile
    private var cachedAd: AppOpenAd? = null

    @Volatile
    private var isLoadingAd: Boolean = false

    @Volatile
    private var isShowingAd: Boolean = false

    @Volatile
    private var adLoadedAtMs: Long = 0L

    @Volatile
    private var lastShownAtMs: Long = 0L

    @Volatile
    private var lastBackgroundedAtMs: Long = 0L

    private var loadRetryAttempt: Int = 0

    init {
        managerScope.launch {
            observeAppOpenAdConfigUseCase().collect { newConfig ->
                val oldConfig = config
                config = newConfig

                Log.d(TAG, "App Open config updated: $newConfig")

                if (!newConfig.enabled) {
                    Log.d(TAG, "App Open disabled by remote config. Clearing cached ad.")
                    clearCurrentAd()
                    return@collect
                }

                if (oldConfig.adUnitId != newConfig.adUnitId) {
                    Log.d(TAG, "App Open ad unit changed. Clearing and loading new ad.")
                    clearCurrentAd()
                    loadAd()
                    return@collect
                }

                if (!isAdReady() && !isLoadingAd) {
                    loadAd()
                }
            }
        }
    }

    fun getCurrentConfig(): AppOpenAdConfig {
        return config
    }

    fun isAdReady(): Boolean {
        return cachedAd != null && !isAdExpired()
    }

    fun onAppMovedToBackground() {
        lastBackgroundedAtMs = now()
    }

    fun loadAd(
        onStateChanged: (AdState) -> Unit = {}
    ) {
        val currentConfig = config

        if (!currentConfig.enabled) {
            onStateChanged(AdState.Skipped("App Open ads are disabled by remote config"))
            return
        }

        if (isLoadingAd) {
            Log.d(TAG, "App Open ad load skipped: load already in progress.")
            return
        }

        if (isAdReady()) {
            Log.d(TAG, "App Open ad load skipped: valid cached ad already exists.")
            onStateChanged(AdState.Loaded)
            return
        }

        startLoading(
            config = currentConfig,
            onStateChanged = onStateChanged
        )
    }

    fun showAdIfAvailable(
        activity: Activity,
        forceShow: Boolean = false,
        onStateChanged: (AdState) -> Unit = {},
        onComplete: () -> Unit = {}
    ) {
        val currentConfig = config

        if (!currentConfig.enabled) {
            onStateChanged(AdState.Skipped("App Open ads are disabled by remote config"))
            onComplete()
            return
        }

        if (activity.isFinishing || activity.isDestroyed) {
            onStateChanged(AdState.Skipped("Activity is not valid for showing ad"))
            onComplete()
            return
        }

        if (isShowingAd) {
            onStateChanged(AdState.Skipped("App Open ad is already showing"))
            onComplete()
            return
        }

        if (!forceShow && !canShowByFrequencyPolicy(currentConfig)) {
            onStateChanged(AdState.Skipped("App Open ad skipped by frequency cap"))
            loadAd()
            onComplete()
            return
        }

        if (!forceShow && !hasEnoughBackgroundTimePassed(currentConfig)) {
            onStateChanged(AdState.Skipped("App foregrounded too quickly"))
            loadAd()
            onComplete()
            return
        }

        if (!isAdReady()) {
            onStateChanged(AdState.Skipped("No App Open ad is ready"))
            loadAd(onStateChanged)
            onComplete()
            return
        }

        presentAd(
            activity = activity,
            onStateChanged = onStateChanged,
            onComplete = onComplete
        )
    }

    private fun startLoading(
        config: AppOpenAdConfig,
        onStateChanged: (AdState) -> Unit
    ) {
        isLoadingAd = true
        onStateChanged(AdState.Loading)

        Log.d(TAG, "Loading App Open ad with unit: ${config.adUnitId}")

        AppOpenAd.load(
            appContext,
            config.adUnitId,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {

                override fun onAdLoaded(ad: AppOpenAd) {
                    cachedAd = ad
                    adLoadedAtMs = now()
                    isLoadingAd = false
                    loadRetryAttempt = 0

                    Log.d(TAG, "App Open ad loaded.")
                    onStateChanged(AdState.Loaded)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    cachedAd = null
                    adLoadedAtMs = 0L
                    isLoadingAd = false

                    Log.e(
                        TAG,
                        "App Open ad failed to load. code=${error.code}, message=${error.message}, domain=${error.domain}"
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
            onStateChanged(AdState.ShowFailed("Cached App Open ad was null"))
            loadAd(onStateChanged)
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
                lastShownAtMs = now()

                Log.d(TAG, "App Open ad is showing.")
                onStateChanged(AdState.Showing)
            }

            override fun onAdImpression() {
                Log.d(TAG, "App Open ad impression recorded.")
                onStateChanged(AdState.Impression)
            }

            override fun onAdClicked() {
                Log.d(TAG, "App Open ad clicked.")
                onStateChanged(AdState.Clicked)
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "App Open ad dismissed.")

                clearCurrentAd()
                onStateChanged(AdState.Dismissed)

                loadAd()
                completeOnce()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.e(
                    TAG,
                    "App Open ad failed to show. code=${error.code}, message=${error.message}"
                )

                clearCurrentAd()
                onStateChanged(AdState.ShowFailed(error.message))

                loadAd()
                completeOnce()
            }
        }

        try {
            ad.show(activity)
        } catch (exception: Exception) {
            Log.e(TAG, "App Open ad show crashed.", exception)

            clearCurrentAd()
            onStateChanged(
                AdState.ShowFailed(
                    exception.message ?: "Unable to show App Open ad"
                )
            )

            loadAd()
            completeOnce()
        }
    }

    private fun scheduleRetryLoad() {
        val currentConfig = config

        if (loadRetryAttempt >= currentConfig.maxLoadRetryCount) {
            Log.d(TAG, "App Open ad retry skipped: max retry count reached.")
            return
        }

        loadRetryAttempt++

        val exponentialDelay = currentConfig.initialRetryDelayMs *
                2.0.pow(loadRetryAttempt - 1).toLong()

        val retryDelay = min(
            exponentialDelay,
            currentConfig.maxRetryDelayMs
        )

        Log.d(TAG, "Scheduling App Open ad retry in $retryDelay ms.")

        managerScope.launch {
            delay(retryDelay)
            loadAd()
        }
    }

    private fun clearCurrentAd() {
        cachedAd?.fullScreenContentCallback = null
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
        config: AppOpenAdConfig
    ): Boolean {
        if (lastShownAtMs == 0L) return true
        return now() - lastShownAtMs >= config.minIntervalBetweenShowsMs
    }

    private fun hasEnoughBackgroundTimePassed(
        config: AppOpenAdConfig
    ): Boolean {
        if (lastBackgroundedAtMs == 0L) {
            return config.showAutomaticallyOnColdStart
        }

        return now() - lastBackgroundedAtMs >= config.minBackgroundDurationBeforeShowMs
    }

    private fun now(): Long = System.currentTimeMillis()

    companion object {
        private const val TAG = "AppOpenAdManager"
    }
}