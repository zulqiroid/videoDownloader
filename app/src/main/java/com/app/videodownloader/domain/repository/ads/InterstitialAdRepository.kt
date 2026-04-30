package com.app.videodownloader.domain.repository.ads

import android.app.Activity
import com.app.videodownloader.domain.model.AdState

/**
 * Abstract contract for interstitial ad operations.
 * Domain layer depends solely on this — never on SDK or data implementations.
 */
interface InterstitialAdRepository {

    /**
     * Loads an interstitial ad if none is cached or loading.
     * Emits [AdState.Loading] → [AdState.Loaded] or [AdState.LoadFailed].
     */
    fun loadAd(onStateChanged: (AdState) -> Unit)

    /**
     * Shows the cached interstitial on the given [activity].
     * Automatically triggers a load if no ad is ready.
     * Emits [AdState.Showing] → [AdState.Dismissed] or [AdState.ShowFailed].
     */
    fun showAd(activity: Activity, onStateChanged: (AdState) -> Unit)

    /** Returns true if a valid, non-expired ad is ready to show. */
    fun isAdReady(): Boolean
}