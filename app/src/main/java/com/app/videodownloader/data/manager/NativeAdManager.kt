package com.app.videodownloader.data.manager

import android.content.Context
import android.util.Log
import com.app.videodownloader.domain.model.ads.AdState
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.usecases.ads.ObserveNativeAdConfigUseCase
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.pow

class NativeAdManager(
    context: Context,
    private val observeNativeAdConfigUseCase: ObserveNativeAdConfigUseCase
) {

    private val appContext = context.applicationContext

    private val managerScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )

    private val _nativeAds = MutableStateFlow<Map<String, NativeAd>>(emptyMap())
    val nativeAds: StateFlow<Map<String, NativeAd>> = _nativeAds.asStateFlow()

    @Volatile
    private var config: NativeAdConfig = NativeAdConfig.default()

    private val loadingPlacements = mutableSetOf<String>()
    private val loadedAtByPlacement = mutableMapOf<String, Long>()
    private val retryAttemptByPlacement = mutableMapOf<String, Int>()
    private val retryJobByPlacement = mutableMapOf<String, Job>()

    init {
        observeRemoteConfig()
    }

    fun getCurrentConfig(): NativeAdConfig {
        return config
    }

    fun preloadEnabledPlacements() {
        val currentConfig = config

        if (!currentConfig.enabled) {
            clearAllAds()
            return
        }

        clearExpiredAds()

        currentConfig.placements
            .filterValues { placementConfig -> placementConfig.enabled }
            .keys
            .forEach { placementKey ->
                loadAd(placementKey)
            }
    }

    fun isAdReady(
        placementKey: String
    ): Boolean {
        val nativeAd = _nativeAds.value[placementKey] ?: return false

        if (isAdExpired(placementKey)) {
            Log.d(TAG, "Native ad expired. Clearing placement=$placementKey")
            clearAd(placementKey)
            return false
        }

        return nativeAd != null
    }

    fun loadAd(
        placementKey: String,
        onStateChanged: (AdState) -> Unit = {}
    ) {
        val currentConfig = config
        val placementConfig = currentConfig.placement(placementKey)

        if (!currentConfig.enabled) {
            onStateChanged(
                AdState.Skipped("Native ads disabled by remote config")
            )
            return
        }

        if (placementConfig == null) {
            onStateChanged(
                AdState.Skipped("Native placement disabled or missing: $placementKey")
            )
            clearAd(placementKey)
            return
        }

        if (loadingPlacements.contains(placementKey)) {
            Log.d(TAG, "Native ad load skipped. Already loading placement=$placementKey")
            return
        }

        clearExpiredAdIfNeeded(placementKey)

        if (isAdReady(placementKey)) {
            Log.d(TAG, "Native ad load skipped. Cached ad is ready placement=$placementKey")
            onStateChanged(AdState.Loaded)
            return
        }

        startLoading(
            placementKey = placementKey,
            requestConfig = currentConfig,
            onStateChanged = onStateChanged
        )
    }

    fun clearAd(
        placementKey: String
    ) {
        retryJobByPlacement.remove(placementKey)?.cancel()
        loadingPlacements.remove(placementKey)
        loadedAtByPlacement.remove(placementKey)
        retryAttemptByPlacement.remove(placementKey)

        val existingAd = _nativeAds.value[placementKey]
        existingAd?.destroy()

        _nativeAds.value = _nativeAds.value
            .toMutableMap()
            .apply {
                remove(placementKey)
            }

        Log.d(TAG, "Native ad cleared placement=$placementKey")
    }

    fun clearAllAds() {
        retryJobByPlacement.values.forEach { job -> job.cancel() }
        retryJobByPlacement.clear()

        loadingPlacements.clear()
        loadedAtByPlacement.clear()
        retryAttemptByPlacement.clear()

        _nativeAds.value.values.forEach { nativeAd ->
            nativeAd.destroy()
        }

        _nativeAds.value = emptyMap()

        Log.d(TAG, "All native ads cleared.")
    }

    private fun observeRemoteConfig() {
        managerScope.launch {
            observeNativeAdConfigUseCase().collect { newConfig ->
                val oldConfig = config
                config = newConfig

                Log.d(TAG, "Native config updated: $newConfig")

                if (!newConfig.enabled) {
                    clearAllAds()
                    return@collect
                }

                if (oldConfig.adUnitId != newConfig.adUnitId) {
                    Log.d(TAG, "Native ad unit changed. Clearing all cached native ads.")
                    clearAllAds()
                } else {
                    clearDisabledPlacements(newConfig)
                    clearExpiredAds()
                }

                preloadEnabledPlacements()
            }
        }
    }

    private fun startLoading(
        placementKey: String,
        requestConfig: NativeAdConfig,
        onStateChanged: (AdState) -> Unit
    ) {
        loadingPlacements.add(placementKey)
        onStateChanged(AdState.Loading)

        Log.d(
            TAG,
            "Loading native ad. placement=$placementKey unit=${requestConfig.adUnitId}"
        )

        val adLoader = AdLoader.Builder(
            appContext,
            requestConfig.adUnitId
        )
            .forNativeAd { loadedAd ->
                loadingPlacements.remove(placementKey)

                val latestConfig = config
                val placementStillEnabled = latestConfig.placement(placementKey) != null
                val adUnitStillSame = latestConfig.adUnitId == requestConfig.adUnitId

                if (!latestConfig.enabled || !placementStillEnabled || !adUnitStillSame) {
                    Log.d(
                        TAG,
                        "Native ad loaded but discarded because config changed. placement=$placementKey"
                    )
                    loadedAd.destroy()
                    onStateChanged(
                        AdState.Skipped("Native ad discarded because config changed")
                    )
                    return@forNativeAd
                }

                val previousAd = _nativeAds.value[placementKey]
                previousAd?.destroy()

                loadedAd.setOnPaidEventListener { adValue ->
                    Log.d(
                        TAG,
                        "Native paid event. placement=$placementKey " +
                                "valueMicros=${adValue.valueMicros}, " +
                                "currency=${adValue.currencyCode}"
                    )
                }

                _nativeAds.value = _nativeAds.value
                    .toMutableMap()
                    .apply {
                        put(placementKey, loadedAd)
                    }

                loadedAtByPlacement[placementKey] = now()
                retryAttemptByPlacement[placementKey] = 0
                retryJobByPlacement.remove(placementKey)?.cancel()

                Log.d(TAG, "Native ad loaded placement=$placementKey")
                onStateChanged(AdState.Loaded)
            }
            .withAdListener(
                object : AdListener() {

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        loadingPlacements.remove(placementKey)

                        Log.e(
                            TAG,
                            "Native ad failed to load. " +
                                    "placement=$placementKey, " +
                                    "code=${error.code}, " +
                                    "message=${error.message}, " +
                                    "domain=${error.domain}"
                        )

                        onStateChanged(
                            AdState.LoadFailed(
                                errorCode = error.code,
                                errorMessage = error.message
                            )
                        )

                        scheduleRetryLoad(placementKey)
                    }

                    override fun onAdClicked() {
                        Log.d(TAG, "Native ad clicked placement=$placementKey")
                        onStateChanged(AdState.Clicked)
                    }

                    override fun onAdImpression() {
                        Log.d(TAG, "Native ad impression placement=$placementKey")
                        onStateChanged(AdState.Impression)
                    }
                }
            )
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    // Keep AdChoices visible and predictable.
                    .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)

                    // Allows both image and video native creatives.
                    .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_ANY)
                    .build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun scheduleRetryLoad(
        placementKey: String
    ) {
        val currentConfig = config

        if (!currentConfig.enabled) return
        if (currentConfig.placement(placementKey) == null) return

        val currentAttempt = retryAttemptByPlacement[placementKey] ?: 0

        if (currentAttempt >= currentConfig.maxLoadRetryCount) {
            Log.d(TAG, "Native retry skipped. Max retry reached placement=$placementKey")
            return
        }

        retryJobByPlacement.remove(placementKey)?.cancel()

        val nextAttempt = currentAttempt + 1
        retryAttemptByPlacement[placementKey] = nextAttempt

        val exponentialDelay = currentConfig.initialRetryDelayMs *
                2.0.pow(nextAttempt - 1).toLong()

        val retryDelay = min(
            exponentialDelay,
            currentConfig.maxRetryDelayMs
        )

        Log.d(
            TAG,
            "Scheduling native retry. placement=$placementKey delayMs=$retryDelay"
        )

        retryJobByPlacement[placementKey] = managerScope.launch {
            delay(retryDelay)
            loadAd(placementKey)
        }
    }

    private fun clearDisabledPlacements(
        newConfig: NativeAdConfig
    ) {
        val enabledPlacementKeys = newConfig.placements
            .filterValues { placementConfig -> placementConfig.enabled }
            .keys

        _nativeAds.value.keys
            .filterNot { placementKey -> placementKey in enabledPlacementKeys }
            .forEach { placementKey ->
                Log.d(TAG, "Clearing disabled native placement=$placementKey")
                clearAd(placementKey)
            }
    }

    private fun clearExpiredAds() {
        _nativeAds.value.keys
            .filter { placementKey -> isAdExpired(placementKey) }
            .forEach { placementKey ->
                Log.d(TAG, "Clearing expired native ad placement=$placementKey")
                clearAd(placementKey)
            }
    }

    private fun clearExpiredAdIfNeeded(
        placementKey: String
    ) {
        if (_nativeAds.value[placementKey] != null && isAdExpired(placementKey)) {
            clearAd(placementKey)
        }
    }

    private fun isAdExpired(
        placementKey: String
    ): Boolean {
        val loadedAt = loadedAtByPlacement[placementKey] ?: return true
        return now() - loadedAt >= config.maxAdCacheDurationMs
    }

    private fun now(): Long = System.currentTimeMillis()

    companion object {
        private const val TAG = "NativeAdManager"
    }
}