package com.allvideodownloader.hdvideodownloader.securevideosaver.data.manager

import android.content.Context
import android.util.Log
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.billing.PremiumAccessController
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.CanRequestAdsUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdConfigUseCase
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
    observeNativeAdConfigUseCase: ObserveNativeAdConfigUseCase,
    private val canRequestAdsUseCase: CanRequestAdsUseCase,
    private val premiumAccessController: PremiumAccessController
) {

    private val appContext = context.applicationContext

    private val managerScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )

    private val _nativeAdPools =
        MutableStateFlow<Map<String, Map<String, NativeAd>>>(emptyMap())

    val nativeAdPools: StateFlow<Map<String, Map<String, NativeAd>>> =
        _nativeAdPools.asStateFlow()

    private val _nativeAds = MutableStateFlow<Map<String, NativeAd>>(emptyMap())

    val nativeAds: StateFlow<Map<String, NativeAd>> =
        _nativeAds.asStateFlow()

    @Volatile
    private var config: NativeAdConfig = NativeAdConfig.default()

    private val loadingSlots = mutableSetOf<AdSlotKey>()
    private val loadedAtBySlot = mutableMapOf<AdSlotKey, Long>()
    private val retryAttemptBySlot = mutableMapOf<AdSlotKey, Int>()
    private val retryJobBySlot = mutableMapOf<AdSlotKey, Job>()

    init {
        managerScope.launch {
            observeNativeAdConfigUseCase().collect { newConfig ->
                val oldConfig = config
                config = newConfig

                Log.d(TAG, "Native config updated: $newConfig")

                if (!newConfig.enabled) {
                    clearAllAds()
                    return@collect
                }

                if (!canRequestAdsUseCase()) {
                    Log.d(TAG, "Native config observed but auto-load blocked: consent not ready.")
                    clearAllAds()
                    return@collect
                }

                if (premiumAccessController.isPremium()) {
                    Log.d(TAG, "Native ads cleared/skipped: premium user.")
                    clearAllAds()
                    return@collect
                }

                if (oldConfig.adUnitId != newConfig.adUnitId) {
                    clearAllAds()
                } else {
                    clearDisabledPlacements(newConfig)
                    clearExpiredAds()
                }
            }
        }
    }

    fun getCurrentConfig(): NativeAdConfig {
        return config
    }

    fun loadAd(
        placementKey: String,
        slotKey: String = NativeAdConfig.DEFAULT_SLOT,
        onStateChanged: (AdState) -> Unit = {}
    ) {
        val currentConfig = config
        val placementConfig = currentConfig.placement(placementKey)
        val key = AdSlotKey(
            placementKey = placementKey,
            slotKey = slotKey
        )

        if (premiumAccessController.isPremium()) {
            onStateChanged(AdState.Skipped("Native ad load skipped: premium user"))
            clearAd(placementKey, slotKey)
            return
        }

        if (!canRequestAdsUseCase()) {
            onStateChanged(AdState.Skipped("Native ad load skipped: consent not ready"))
            clearAd(placementKey, slotKey)
            return
        }

        if (!currentConfig.enabled) {
            onStateChanged(AdState.Skipped("Native ads disabled"))
            return
        }

        if (placementConfig == null) {
            onStateChanged(AdState.Skipped("Native placement disabled or missing: $placementKey"))
            clearAd(placementKey, slotKey)
            return
        }

        if (loadingSlots.contains(key)) {
            Log.d(TAG, "Native load skipped. Already loading $key")
            return
        }

        clearExpiredAdIfNeeded(key)

        if (isAdReady(placementKey, slotKey)) {
            onStateChanged(AdState.Loaded)
            return
        }

        startLoading(
            key = key,
            requestConfig = currentConfig,
            onStateChanged = onStateChanged
        )
    }

    fun isAdReady(
        placementKey: String,
        slotKey: String = NativeAdConfig.DEFAULT_SLOT
    ): Boolean {
        val key = AdSlotKey(placementKey, slotKey)

        _nativeAdPools.value[placementKey]?.get(slotKey)
            ?: return false

        if (isAdExpired(key)) {
            clearAd(placementKey, slotKey)
            return false
        }

        return true
    }

    fun clearAd(
        placementKey: String,
        slotKey: String = NativeAdConfig.DEFAULT_SLOT
    ) {
        val key = AdSlotKey(placementKey, slotKey)

        retryJobBySlot.remove(key)?.cancel()
        retryAttemptBySlot.remove(key)
        loadedAtBySlot.remove(key)
        loadingSlots.remove(key)

        val existingAd = _nativeAdPools.value[placementKey]?.get(slotKey)
        existingAd?.destroy()

        val updatedPools = _nativeAdPools.value.toMutableMap()
        val placementAds = updatedPools[placementKey]?.toMutableMap()

        placementAds?.remove(slotKey)

        if (placementAds.isNullOrEmpty()) {
            updatedPools.remove(placementKey)
        } else {
            updatedPools[placementKey] = placementAds
        }

        _nativeAdPools.value = updatedPools
        syncDefaultNativeAds()

        Log.d(TAG, "Native ad cleared. placement=$placementKey slot=$slotKey")
    }

    fun clearPlacement(placementKey: String) {
        val slotKeys = _nativeAdPools.value[placementKey]?.keys.orEmpty()

        slotKeys.forEach { slotKey ->
            clearAd(
                placementKey = placementKey,
                slotKey = slotKey
            )
        }
    }

    fun clearAllAds() {
        retryJobBySlot.values.forEach { it.cancel() }
        retryJobBySlot.clear()
        retryAttemptBySlot.clear()
        loadedAtBySlot.clear()
        loadingSlots.clear()

        _nativeAdPools.value.values.forEach { slotMap ->
            slotMap.values.forEach { nativeAd ->
                nativeAd.destroy()
            }
        }

        _nativeAdPools.value = emptyMap()
        _nativeAds.value = emptyMap()

        Log.d(TAG, "All native ads cleared")
    }

    private fun startLoading(
        key: AdSlotKey,
        requestConfig: NativeAdConfig,
        onStateChanged: (AdState) -> Unit
    ) {
        loadingSlots.add(key)
        onStateChanged(AdState.Loading)

        val placementConfig = requestConfig.placement(key.placementKey)

        if (placementConfig == null) {
            loadingSlots.remove(key)
            onStateChanged(AdState.Skipped("Placement config missing"))
            return
        }

        val adUnitId = placementConfig.unitId.ifBlank {
            requestConfig.adUnitId
        }

        if (adUnitId.isBlank()) {
            loadingSlots.remove(key)
            onStateChanged(AdState.Skipped("Native ad unit ID missing"))
            Log.e(TAG, "Native ad unit ID missing for $key")
            return
        }

        Log.d(
            TAG,
            "Loading native ad. placement=${key.placementKey}, slot=${key.slotKey}, adUnitId=$adUnitId"
        )

        val adLoader = AdLoader.Builder(
            appContext,
            adUnitId
        )
            .forNativeAd { loadedAd ->
                loadingSlots.remove(key)

                val latestConfig = config
                val latestPlacementConfig = latestConfig.placement(key.placementKey)

                val placementStillEnabled = latestPlacementConfig != null

                val latestAdUnitId = latestPlacementConfig?.unitId?.ifBlank {
                    latestConfig.adUnitId
                }

                val adUnitStillSame = latestAdUnitId == adUnitId

                if (!latestConfig.enabled || !placementStillEnabled || !adUnitStillSame) {
                    loadedAd.destroy()
                    onStateChanged(
                        AdState.Skipped("Native ad discarded because config changed")
                    )

                    Log.d(
                        TAG,
                        "Native ad discarded. key=$key, loadedAdUnitId=$adUnitId, latestAdUnitId=$latestAdUnitId"
                    )
                    return@forNativeAd
                }

                putLoadedAd(
                    key = key,
                    nativeAd = loadedAd
                )

                loadedAd.setOnPaidEventListener { adValue ->
                    Log.d(
                        TAG,
                        "Native paid event. $key valueMicros=${adValue.valueMicros}, currency=${adValue.currencyCode}"
                    )
                }

                loadedAtBySlot[key] = now()
                retryAttemptBySlot[key] = 0
                retryJobBySlot.remove(key)?.cancel()

                Log.d(TAG, "Native ad loaded: $key")
                onStateChanged(AdState.Loaded)
            }
            .withAdListener(
                object : AdListener() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        loadingSlots.remove(key)

                        Log.e(
                            TAG,
                            """
                            Native failed.
                            placement=${key.placementKey}
                            slot=${key.slotKey}
                            adUnitId=$adUnitId
                            code=${error.code}
                            domain=${error.domain}
                            message=${error.message}
                            responseInfo=${error.responseInfo}
                            cause=${error.cause}
                            """.trimIndent()
                        )

                        onStateChanged(
                            AdState.LoadFailed(
                                errorCode = error.code,
                                errorMessage = error.message
                            )
                        )

                        scheduleRetryLoad(key)
                    }

                    override fun onAdClicked() {
                        onStateChanged(AdState.Clicked)
                    }

                    override fun onAdImpression() {
                        onStateChanged(AdState.Impression)
                    }
                }
            )
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                    .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_ANY)
                    .build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun putLoadedAd(
        key: AdSlotKey,
        nativeAd: NativeAd
    ) {
        val currentPlacementAds =
            _nativeAdPools.value[key.placementKey]?.toMutableMap()
                ?: mutableMapOf()

        currentPlacementAds[key.slotKey]?.destroy()
        currentPlacementAds[key.slotKey] = nativeAd

        _nativeAdPools.value = _nativeAdPools.value
            .toMutableMap()
            .apply {
                put(key.placementKey, currentPlacementAds)
            }

        syncDefaultNativeAds()
    }

    private fun scheduleRetryLoad(key: AdSlotKey) {
        val currentConfig = config

        if (premiumAccessController.isPremium()) return
        if (!currentConfig.enabled) return
        if (!canRequestAdsUseCase()) return
        if (currentConfig.placement(key.placementKey) == null) return

        val currentAttempt = retryAttemptBySlot[key] ?: 0

        if (currentAttempt >= currentConfig.maxLoadRetryCount) {
            Log.d(TAG, "Native retry skipped. Max retry reached: $key")
            return
        }

        retryJobBySlot.remove(key)?.cancel()

        val nextAttempt = currentAttempt + 1
        retryAttemptBySlot[key] = nextAttempt

        val exponentialDelay =
            currentConfig.initialRetryDelayMs * 2.0.pow(nextAttempt - 1).toLong()

        val retryDelay = min(
            exponentialDelay,
            currentConfig.maxRetryDelayMs
        )

        retryJobBySlot[key] = managerScope.launch {
            delay(retryDelay)
            loadAd(
                placementKey = key.placementKey,
                slotKey = key.slotKey
            )
        }
    }

    private fun clearDisabledPlacements(newConfig: NativeAdConfig) {
        val enabledPlacementKeys = newConfig.placements
            .filterValues { it.enabled }
            .keys

        _nativeAdPools.value.keys
            .filterNot { it in enabledPlacementKeys }
            .forEach { placementKey ->
                clearPlacement(placementKey)
            }
    }

    private fun clearExpiredAds() {
        loadedAtBySlot.keys
            .filter { isAdExpired(it) }
            .forEach { key ->
                clearAd(
                    placementKey = key.placementKey,
                    slotKey = key.slotKey
                )
            }
    }

    private fun clearExpiredAdIfNeeded(key: AdSlotKey) {
        if (isAdExpired(key)) {
            clearAd(
                placementKey = key.placementKey,
                slotKey = key.slotKey
            )
        }
    }

    private fun isAdExpired(key: AdSlotKey): Boolean {
        val loadedAt = loadedAtBySlot[key] ?: return false
        return now() - loadedAt >= config.maxAdCacheDurationMs
    }

    private fun syncDefaultNativeAds() {
        _nativeAds.value = _nativeAdPools.value.mapNotNull { entry ->
            val defaultAd = entry.value[NativeAdConfig.DEFAULT_SLOT]
            if (defaultAd != null) {
                entry.key to defaultAd
            } else {
                null
            }
        }.toMap()
    }

    private fun now(): Long = System.currentTimeMillis()

    private data class AdSlotKey(
        val placementKey: String,
        val slotKey: String
    )

    companion object {
        private const val TAG = "NativeAdManager"
    }
}