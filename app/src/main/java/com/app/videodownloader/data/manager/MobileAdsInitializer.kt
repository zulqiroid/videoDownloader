package com.app.videodownloader.data.manager

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume

class MobileAdsInitializer(
    private val context: Context
) {
    private val initialized = AtomicBoolean(false)

    suspend fun initializeIfNeeded(): Boolean {
        if (initialized.get()) return true

        return withContext(Dispatchers.Main.immediate) {
            suspendCancellableCoroutine { continuation ->
                MobileAds.initialize(context.applicationContext) {
                    initialized.set(true)
                    Log.d(TAG, "Google Mobile Ads SDK initialized after consent.")

                    if (continuation.isActive) {
                        continuation.resume(true)
                    }
                }
            }
        }
    }

    fun isInitialized(): Boolean {
        return initialized.get()
    }

    companion object {
        private const val TAG = "MobileAdsInitializer"
    }
}