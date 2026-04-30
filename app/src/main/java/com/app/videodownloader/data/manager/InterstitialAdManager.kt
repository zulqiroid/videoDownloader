package com.app.videodownloader.data.manager

import android.app.Activity
import android.content.Context
import android.util.Log
import com.app.videodownloader.domain.model.AdState
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Low-level wrapper around the Google Mobile Ads SDK for Interstitial Ads.
 *
 * Responsibilities:
 *  - Load and cache a single [InterstitialAd] instance.
 *  - Guard against duplicate loads or concurrent show attempts.
 *  - Expose all lifecycle transitions through a typed [AdState] callback.
 *
 * Interstitial ads do NOT have a TTL expiry like App Open Ads,
 * but we still invalidate on dismiss/failure to enforce one-ad-at-a-time.
 */
class InterstitialAdManager(
    private val context: Context
) {

    // ─────────────────────────────────────────────────────────────────────────
    // Constants
    // ─────────────────────────────────────────────────────────────────────────

    companion object {
        private const val TAG = "InterstitialAdManager"
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712" // test ID
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internal State
    // ─────────────────────────────────────────────────────────────────────────

    @Volatile private var cachedAd: InterstitialAd? = null
    @Volatile private var isLoadInProgress: Boolean = false
    @Volatile private var isAdCurrentlyShowing: Boolean = false

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    fun isAdReady(): Boolean = cachedAd != null

    fun loadAd(onStateChanged: (AdState) -> Unit) {
        when {
            isLoadInProgress -> {
                Log.d(TAG, "Load already in progress — skipping.")
                return
            }
            isAdReady() -> {
                Log.d(TAG, "Ad already cached — skipping load.")
                onStateChanged(AdState.Loaded)
                return
            }
            else -> startLoading(onStateChanged)
        }
    }

    fun showAd(activity: Activity, onStateChanged: (AdState) -> Unit) {
        when {
            isAdCurrentlyShowing -> {
                Log.w(TAG, "Ad already on screen — ignoring duplicate show request.")
                return
            }
            !isAdReady() -> {
                Log.d(TAG, "No ad ready — triggering load before next opportunity.")
                loadAd(onStateChanged)
                return
            }
            else -> presentAd(activity, onStateChanged)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun startLoading(onStateChanged: (AdState) -> Unit) {
        isLoadInProgress = true
        onStateChanged(AdState.Loading)
        Log.d(TAG, "Requesting new Interstitial Ad…")

        InterstitialAd.load(
            context,
            AD_UNIT_ID,
            AdRequest.Builder().build(),
            buildLoadCallback(onStateChanged)
        )
    }

    private fun buildLoadCallback(
        onStateChanged: (AdState) -> Unit
    ): InterstitialAdLoadCallback =
        object : InterstitialAdLoadCallback() {

            override fun onAdLoaded(ad: InterstitialAd) {
                cachedAd = ad
                isLoadInProgress = false
                Log.d(TAG, "Interstitial ad loaded successfully.")
                onStateChanged(AdState.Loaded)
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                isLoadInProgress = false
                Log.e(TAG, buildLoadErrorLog(error))
                onStateChanged(AdState.LoadFailed(error.code, error.message))
            }
        }

    private fun presentAd(activity: Activity, onStateChanged: (AdState) -> Unit) {
        val ad = cachedAd ?: run {
            Log.e(TAG, "presentAd() called but cachedAd is null.")
            onStateChanged(AdState.ShowFailed("Cached ad was null at presentation time."))
            return
        }

        ad.fullScreenContentCallback = buildFullScreenCallback(onStateChanged)
        ad.show(activity)
    }

    private fun buildFullScreenCallback(
        onStateChanged: (AdState) -> Unit
    ): FullScreenContentCallback =
        object : FullScreenContentCallback() {

            override fun onAdShowedFullScreenContent() {
                isAdCurrentlyShowing = true
                Log.d(TAG, "Interstitial ad is now showing.")
                onStateChanged(AdState.Showing)
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Interstitial ad dismissed.")
                resetAdState()
                onStateChanged(AdState.Dismissed)
                // Preload next ad immediately so it's ready at the next trigger point
                loadAd { /* fire-and-forget background preload */ }
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.e(TAG, "Interstitial failed to show: [${error.code}] ${error.message}")
                resetAdState()
                onStateChanged(AdState.ShowFailed(error.message))
                loadAd { /* recovery load */ }
            }
        }

    private fun resetAdState() {
        cachedAd = null
        isAdCurrentlyShowing = false
    }

    private fun buildLoadErrorLog(error: LoadAdError): String = buildString {
        appendLine("Interstitial ad failed to load.")
        appendLine("  Code    : ${error.code}")
        appendLine("  Message : ${error.message}")
        appendLine("  Domain  : ${error.domain}")
        appendLine("  Cause   : ${error.cause}")
        appendLine("  Response: ${error.responseInfo}")
    }
}