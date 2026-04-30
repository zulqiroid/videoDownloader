package com.app.videodownloader.domain.repository.ads

import android.app.Activity
import com.app.videodownloader.domain.model.AdState

/**
 * Contract defining all ad-related operations.
 * The domain layer depends ONLY on this abstraction — never on the data layer.
 */
interface AdRepository {

    /**
     * Initiates ad loading if no valid ad is already cached.
     * Emits [com.app.videodownloader.domain.model.AdState.Loading], then [com.app.videodownloader.domain.model.AdState.Loaded] or [com.app.videodownloader.domain.model.AdState.LoadFailed].
     *
     * @param onStateChanged Callback invoked on every state transition.
     */
    fun loadAd(onStateChanged: (AdState) -> Unit)

    /**
     * Displays the loaded ad on the given [activity].
     * Triggers [AdState.Showing], [AdState.Dismissed], or [AdState.ShowFailed].
     * If no ad is ready, automatically triggers [loadAd] first.
     *
     * @param activity      The host activity for the full-screen ad.
     * @param onStateChanged Callback invoked on every state transition.
     */
    fun showAd(activity: Activity, onStateChanged: (AdState) -> Unit)

    /**
     * Returns true if a valid, non-expired ad is ready to be shown.
     */
    fun isAdReady(): Boolean
}