package com.app.videodownloader.data.manager

import android.app.Activity
import android.content.Context
import android.util.Log
import com.app.videodownloader.domain.repository.ads.AdManager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import com.app.videodownloader.domain.model.AdState
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError


/**
 * Low-level wrapper around the Google Mobile Ads SDK for App Open Ads.
 *
 * Responsibilities:
 *  - Load and cache a single [AppOpenAd] instance.
 *  - Track expiry (4-hour TTL per Google's recommendation).
 *  - Guard against duplicate loads or concurrent show attempts.
 *  - Surface all state transitions through a typed [AdState] callback.
 *
 * This class has NO knowledge of use cases, ViewModels, or repositories.
 * It is a pure infrastructure concern.
 */
 class AppOpenAdManager(
    private val context: Context
) {

    // ─────────────────────────────────────────────────────────────────────────
    // Constants
    // ─────────────────────────────────────────────────────────────────────────

    companion object {
        private const val TAG = "AppOpenAdManager"
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"
        private const val AD_EXPIRY_DURATION_MS = 4 * 60 * 60 * 1000L // 4 hours
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internal State
    // ─────────────────────────────────────────────────────────────────────────

    @Volatile private var cachedAd: AppOpenAd? = null
    @Volatile private var isLoadInProgress: Boolean = false
    @Volatile private var isAdCurrentlyShowing: Boolean = false
    @Volatile private var adLoadedAtMs: Long = 0L

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns true if an unexpired ad is cached and ready to display.
     */
    fun isAdReady(): Boolean =
        cachedAd != null && !isAdExpired()

    /**
     * Loads a new App Open Ad if none is cached or in-flight.
     *
     * @param onStateChanged Notified with [AdState.Loading], [AdState.Loaded],
     *                       or [AdState.LoadFailed].
     */
    fun loadAd(onStateChanged: (AdState) -> Unit) {
        when {
            isLoadInProgress -> {
                Log.d(TAG, "Load already in progress — skipping.")
                return
            }
            isAdReady() -> {
                Log.d(TAG, "Valid ad already cached — skipping load.")
                onStateChanged(AdState.Loaded)
                return
            }
            else -> startLoading(onStateChanged)
        }
    }

    /**
     * Displays the cached ad on the provided [activity].
     * If no ad is ready, kicks off a fresh load and notifies via [onStateChanged].
     *
     * @param activity       The resumed [Activity] to host the full-screen overlay.
     * @param onStateChanged Notified with [AdState.Showing], [AdState.Dismissed],
     *                       or [AdState.ShowFailed].
     */
    fun showAd(activity: Activity, onStateChanged: (AdState) -> Unit) {
        when {
            isAdCurrentlyShowing -> {
                Log.w(TAG, "Ad is already on screen — ignoring show request.")
                return
            }
            !isAdReady() -> {
                Log.d(TAG, "No ad ready — initiating load before next show attempt.")
                loadAd(onStateChanged)
                return
            }
            else -> presentAd(activity, onStateChanged)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun isAdExpired(): Boolean =
        System.currentTimeMillis() - adLoadedAtMs >= AD_EXPIRY_DURATION_MS

    private fun startLoading(onStateChanged: (AdState) -> Unit) {
        isLoadInProgress = true
        onStateChanged(AdState.Loading)
        Log.d(TAG, "Requesting new App Open Ad…")

        AppOpenAd.load(
            context,
            AD_UNIT_ID,
            AdRequest.Builder().build(),
            buildLoadCallback(onStateChanged)
        )
    }

    private fun buildLoadCallback(
        onStateChanged: (AdState) -> Unit
    ): AppOpenAd.AppOpenAdLoadCallback =
        object : AppOpenAd.AppOpenAdLoadCallback() {

            override fun onAdLoaded(ad: AppOpenAd) {
                cachedAd = ad
                adLoadedAtMs = System.currentTimeMillis()
                isLoadInProgress = false
                Log.d(TAG, "Ad loaded successfully.")
                onStateChanged(AdState.Loaded)
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                isLoadInProgress = false
                Log.e(TAG, buildLoadErrorLog(error))
                onStateChanged(AdState.LoadFailed(error.code, error.message))
            }
        }

    private fun presentAd(activity: Activity, onStateChanged: (AdState) -> Unit) {
        cachedAd?.fullScreenContentCallback = buildFullScreenCallback(onStateChanged)
        cachedAd?.show(activity) ?: run {
            Log.e(TAG, "show() called but cachedAd is null — this should not happen.")
            onStateChanged(AdState.ShowFailed("Cached ad was null at presentation time."))
        }
    }

    private fun buildFullScreenCallback(
        onStateChanged: (AdState) -> Unit
    ): FullScreenContentCallback =
        object : FullScreenContentCallback() {

            override fun onAdShowedFullScreenContent() {
                isAdCurrentlyShowing = true
                Log.d(TAG, "Ad is now showing full screen.")
                onStateChanged(AdState.Showing)
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad dismissed by user.")
                resetAdState()
                onStateChanged(AdState.Dismissed)
                // Proactively load the next ad so it's ready on the next app foreground.
                loadAd { /* fire-and-forget background preload */ }
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.e(TAG, "Ad failed to show: [${error.code}] ${error.message}")
                resetAdState()
                onStateChanged(AdState.ShowFailed(error.message))
                loadAd { /* attempt recovery load */ }
            }
        }

    private fun resetAdState() {
        cachedAd = null
        isAdCurrentlyShowing = false
        adLoadedAtMs = 0L
    }

    private fun buildLoadErrorLog(error: LoadAdError): String = buildString {
        appendLine("Ad failed to load.")
        appendLine("  Code    : ${error.code}")
        appendLine("  Message : ${error.message}")
        appendLine("  Domain  : ${error.domain}")
        appendLine("  Cause   : ${error.cause}")
        appendLine("  Response: ${error.responseInfo}")
    }
}