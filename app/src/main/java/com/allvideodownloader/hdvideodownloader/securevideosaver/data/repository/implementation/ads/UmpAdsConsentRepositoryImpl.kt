package com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.ads

import android.app.Activity
import android.content.Context
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdsConsentResult
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.ads.AdsConsentRepository
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class UmpAdsConsentRepositoryImpl(
    private val context: Context,
    private val isDebug: Boolean
) : AdsConsentRepository {

    private val consentInformation: ConsentInformation by lazy {
        UserMessagingPlatform.getConsentInformation(context.applicationContext)
    }

    override suspend fun requestConsent(
        activity: Activity
    ): AdsConsentResult {
        return withContext(Dispatchers.Main.immediate) {
            val params = buildConsentRequestParameters(activity)

            val updateResult = requestConsentInfoUpdate(
                activity = activity,
                params = params
            )

            if (!updateResult.success) {
                return@withContext AdsConsentResult(
                    canRequestAds = consentInformation.canRequestAds(),
                    privacyOptionsRequired = isPrivacyOptionsRequired(),
                    errorMessage = updateResult.errorMessage
                )
            }

            val formResult = loadAndShowConsentFormIfRequired(activity)

            AdsConsentResult(
                canRequestAds = consentInformation.canRequestAds(),
                privacyOptionsRequired = isPrivacyOptionsRequired(),
                errorMessage = formResult.errorMessage
            )
        }
    }

    override suspend fun showPrivacyOptionsForm(
        activity: Activity
    ): AdsConsentResult {
        return withContext(Dispatchers.Main.immediate) {
            val formResult = suspendCancellableCoroutine<ConsentFormResult> { continuation ->
                UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
                    if (continuation.isActive) {
                        continuation.resume(
                            ConsentFormResult(
                                errorMessage = formError?.message
                            )
                        )
                    }
                }
            }

            AdsConsentResult(
                canRequestAds = consentInformation.canRequestAds(),
                privacyOptionsRequired = isPrivacyOptionsRequired(),
                errorMessage = formResult.errorMessage
            )
        }
    }

    override fun canRequestAds(): Boolean {
        return consentInformation.canRequestAds()
    }

    override fun isPrivacyOptionsRequired(): Boolean {
        return consentInformation.privacyOptionsRequirementStatus ==
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }

    override fun resetConsentForTesting() {
        if (isDebug) {
            consentInformation.reset()
        }
    }

    private fun buildConsentRequestParameters(
        activity: Activity
    ): ConsentRequestParameters {
        val builder = ConsentRequestParameters.Builder()

        if (isDebug) {
            val debugSettings = ConsentDebugSettings.Builder(activity)
                .setDebugGeography(
                    ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA
                )
                /*
                 * After first test run, check Logcat for your hashed test device ID
                 * and add:
                 * .addTestDeviceHashedId("YOUR_HASHED_DEVICE_ID")
                 */
                .build()

            builder.setConsentDebugSettings(debugSettings)
        }

        return builder.build()
    }

    private suspend fun requestConsentInfoUpdate(
        activity: Activity,
        params: ConsentRequestParameters
    ): ConsentUpdateResult {
        return suspendCancellableCoroutine { continuation ->
            consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                {
                    if (continuation.isActive) {
                        continuation.resume(
                            ConsentUpdateResult(
                                success = true,
                                errorMessage = null
                            )
                        )
                    }
                },
                { formError ->
                    if (continuation.isActive) {
                        continuation.resume(
                            ConsentUpdateResult(
                                success = false,
                                errorMessage = formError.message
                            )
                        )
                    }
                }
            )
        }
    }

    private suspend fun loadAndShowConsentFormIfRequired(
        activity: Activity
    ): ConsentFormResult {
        return suspendCancellableCoroutine { continuation ->
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                activity
            ) { formError ->
                if (continuation.isActive) {
                    continuation.resume(
                        ConsentFormResult(
                            errorMessage = formError?.message
                        )
                    )
                }
            }
        }
    }

    private data class ConsentUpdateResult(
        val success: Boolean,
        val errorMessage: String?
    )

    private data class ConsentFormResult(
        val errorMessage: String?
    )
}