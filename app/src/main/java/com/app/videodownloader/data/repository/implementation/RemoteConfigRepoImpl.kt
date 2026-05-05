package com.app.videodownloader.data.repository.implementation

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.app.videodownloader.core.utils.DataStoreKeys
import com.app.videodownloader.core.utils.RemoteConfigKeys
import com.app.videodownloader.domain.model.ApiKey
import com.app.videodownloader.domain.model.ads.AppOpenAdConfig
import com.app.videodownloader.domain.model.ads.BannerAdConfig
import com.app.videodownloader.domain.model.ads.InterstitialAdConfig
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.model.ads.NativeAdPlacementConfig
import com.app.videodownloader.domain.repository.RemoteConfigRepository
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class RemoteConfigRepoImpl(
    private val dataStore: DataStore<Preferences>
) : RemoteConfigRepository {

    private val remoteConfig: FirebaseRemoteConfig by lazy {
        Firebase.remoteConfig
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    private val defaultAppOpenAdConfig = AppOpenAdConfig.default()
    private val _appOpenAdConfig = MutableStateFlow(defaultAppOpenAdConfig)
    override val appOpenAdConfig = _appOpenAdConfig.asStateFlow()

    private val defaultInterstitialAdConfig = InterstitialAdConfig.default()
    private val _interstitialAdConfig = MutableStateFlow(defaultInterstitialAdConfig)
    override val interstitialAdConfig = _interstitialAdConfig.asStateFlow()

    private val defaultBannerAdConfig = BannerAdConfig.default()
    private val _bannerAdConfig = MutableStateFlow(defaultBannerAdConfig)
    override val bannerAdConfig = _bannerAdConfig.asStateFlow()

    private val defaultNativeAdConfig = NativeAdConfig.default()
    private val _nativeAdConfig = MutableStateFlow(defaultNativeAdConfig)
    override val nativeAdConfig = _nativeAdConfig.asStateFlow()

    override suspend fun initializeAndFetch(
        onComplete: () -> Unit
    ) {
        Log.d(TAG, "Initializing Remote Config")

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0 // Development. Use 3600 for production.
            fetchTimeoutInSeconds = 60
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
            .addOnCompleteListener { settingsTask ->
                if (settingsTask.isSuccessful) {
                    Log.d(TAG, "Remote config settings applied successfully")

                    setDefaultsAndFetch(
                        onComplete = onComplete
                    )
                } else {
                    Log.e(TAG, "Remote config settings failed", settingsTask.exception)

                    readAndPublishConfigs()
                    onComplete()
                }
            }
    }

    override suspend fun getApiSecretKey(): String? {
        val data = dataStore.data.first()
        return data[DataStoreKeys.API_SECRET_KEY]
    }

    override suspend fun getBaseUrl(): String? {
        val data = dataStore.data.first()
        return data[DataStoreKeys.BASE_API_URL]
    }

    override fun getCurrentAppOpenAdConfig(): AppOpenAdConfig {
        return _appOpenAdConfig.value
    }

    override fun getCurrentInterstitialAdConfig(): InterstitialAdConfig {
        return _interstitialAdConfig.value
    }

    override fun getCurrentBannerAdConfig(): BannerAdConfig {
        return _bannerAdConfig.value
    }

    override fun getCurrentNativeAdConfig(): NativeAdConfig {
        return _nativeAdConfig.value
    }

    override suspend fun getPremiumIconVisibility(): Boolean {
        val data = dataStore.data.first()
        return data[DataStoreKeys.SHOW_PREMIUM_ICON] ?: true
    }

    private fun setDefaultsAndFetch(
        onComplete: () -> Unit
    ) {
        remoteConfig.setDefaultsAsync(
            mapOf(
                RemoteConfigKeys.API_SECRET_KEY_VALUE_REMOTE to "",
                 RemoteConfigKeys.BASE_URL_REMOTE to "",
                RemoteConfigKeys.SHOW_PREMIUM_ICON_REMOTE to true,
                RemoteConfigKeys.ADS_APP_OPEN_CONFIG_REMOTE to defaultAppOpenAdConfig.toDefaultJson(),
                RemoteConfigKeys.ADS_INTERSTITIAL_CONFIG_REMOTE to defaultInterstitialAdConfig.toDefaultJson(),
                RemoteConfigKeys.ADS_BANNER_CONFIG_REMOTE to defaultBannerAdConfig.toDefaultJson(),
                RemoteConfigKeys.ADS_NATIVE_CONFIG_REMOTE to defaultNativeAdConfig.toDefaultJson()
            )
        ).addOnCompleteListener { defaultsTask ->
            if (!defaultsTask.isSuccessful) {
                Log.e(TAG, "Remote config defaults failed", defaultsTask.exception)
            }

            remoteConfig.fetchAndActivate()
                .addOnCompleteListener { fetchTask ->
                    if (fetchTask.isSuccessful) {
                        Log.d(
                            TAG,
                            "Remote config fetched and activated: ${fetchTask.result}"
                        )
                    } else {
                        Log.e(TAG, "Remote config fetch failed", fetchTask.exception)
                    }

                    logAllRemoteConfigValues()
                    readAndPublishConfigs()

                    CoroutineScope(Dispatchers.IO).launch {
                        saveConfigToDataStore()
                        onComplete()
                    }
                }
        }
    }

    private fun readAndPublishConfigs() {
        _appOpenAdConfig.value = readAppOpenAdConfig()
        _interstitialAdConfig.value = readInterstitialAdConfig()
        _bannerAdConfig.value = readBannerAdConfig()
        _nativeAdConfig.value = readNativeAdConfig()

        Log.d(TAG, "App Open config published: ${_appOpenAdConfig.value}")
        Log.d(TAG, "Interstitial config published: ${_interstitialAdConfig.value}")
        Log.d(TAG, "Banner config published: ${_bannerAdConfig.value}")
        Log.d(TAG, "Native config published: ${_nativeAdConfig.value}")
    }

    private fun readInterstitialAdConfig(): InterstitialAdConfig {
        val value = remoteConfig.getValue(
            RemoteConfigKeys.ADS_INTERSTITIAL_CONFIG_REMOTE
        )

        val rawJson = value.asString()

        Log.d(TAG, "Interstitial RC key = ${RemoteConfigKeys.ADS_INTERSTITIAL_CONFIG_REMOTE}")
        Log.d(TAG, "Interstitial RC source = ${value.source}")
        Log.d(TAG, "Interstitial RC raw JSON = $rawJson")

        if (rawJson.isBlank()) {
            Log.w(TAG, "Interstitial config JSON is blank. Using default config.")
            return defaultInterstitialAdConfig
        }

        return try {
            val parsedConfig = json.decodeFromString<InterstitialAdConfig>(rawJson)
            parsedConfig.sanitized()
        } catch (exception: Exception) {
            Log.e(TAG, "Failed to parse Interstitial config JSON. Using default config.", exception)
            defaultInterstitialAdConfig
        }
    }

    private fun InterstitialAdConfig.sanitized(): InterstitialAdConfig {
        return copy(
            adUnitId = adUnitId.ifBlank {
                InterstitialAdConfig.TEST_INTERSTITIAL_AD_UNIT_ID
            },
            minIntervalBetweenShowsMs = minIntervalBetweenShowsMs.coerceAtLeast(0L),
            maxAdCacheDurationMs = maxAdCacheDurationMs.coerceAtLeast(MIN_AD_CACHE_DURATION_MS),
            maxLoadRetryCount = maxLoadRetryCount.coerceIn(0, MAX_RETRY_COUNT),
            initialRetryDelayMs = initialRetryDelayMs.coerceAtLeast(MIN_INITIAL_RETRY_DELAY_MS),
            maxRetryDelayMs = maxRetryDelayMs.coerceAtLeast(MIN_MAX_RETRY_DELAY_MS),
            tabSwitchTriggerCount = tabSwitchTriggerCount.coerceAtLeast(1),
            playMediaTriggerCount = playMediaTriggerCount.coerceAtLeast(1),
            downloadClickTriggerCount = downloadClickTriggerCount.coerceAtLeast(1),
            reelOpenTriggerCount = reelOpenTriggerCount.coerceAtLeast(1),
            socialOpenTriggerCount = socialOpenTriggerCount.coerceAtLeast(1),
            backNavigationTriggerCount = backNavigationTriggerCount.coerceAtLeast(1)
        )
    }

    private fun InterstitialAdConfig.toDefaultJson(): String {
        return json.encodeToString(
            serializer = InterstitialAdConfig.serializer(),
            value = this
        )
    }

    private fun readAppOpenAdConfig(): AppOpenAdConfig {
        val value = remoteConfig.getValue(
            RemoteConfigKeys.ADS_APP_OPEN_CONFIG_REMOTE
        )

        val rawJson = value.asString()

        Log.d(TAG, "App Open RC key = ${RemoteConfigKeys.ADS_APP_OPEN_CONFIG_REMOTE}")
        Log.d(TAG, "App Open RC source = ${value.source}")
        Log.d(TAG, "App Open RC raw JSON = $rawJson")

        if (rawJson.isBlank()) {
            Log.w(TAG, "App Open config JSON is blank. Using default config.")
            return defaultAppOpenAdConfig
        }

        return try {
            val parsedConfig = json.decodeFromString<AppOpenAdConfig>(rawJson)
            parsedConfig.sanitized()
        } catch (exception: SerializationException) {
            Log.e(TAG, "Failed to parse App Open config JSON. Using default config.", exception)
            defaultAppOpenAdConfig
        } catch (exception: IllegalArgumentException) {
            Log.e(TAG, "Invalid App Open config JSON. Using default config.", exception)
            defaultAppOpenAdConfig
        }
    }

    private fun AppOpenAdConfig.sanitized(): AppOpenAdConfig {
        return copy(
            adUnitId = adUnitId.ifBlank {
                AppOpenAdConfig.TEST_APP_OPEN_AD_UNIT_ID
            },
            maxAdCacheDurationMs = maxAdCacheDurationMs.coerceAtLeast(MIN_AD_CACHE_DURATION_MS),
            minIntervalBetweenShowsMs = minIntervalBetweenShowsMs.coerceAtLeast(0L),
            minBackgroundDurationBeforeShowMs = minBackgroundDurationBeforeShowMs.coerceAtLeast(0L),
            maxLoadRetryCount = maxLoadRetryCount.coerceIn(0, MAX_RETRY_COUNT),
            initialRetryDelayMs = initialRetryDelayMs.coerceAtLeast(MIN_INITIAL_RETRY_DELAY_MS),
            maxRetryDelayMs = maxRetryDelayMs.coerceAtLeast(MIN_MAX_RETRY_DELAY_MS)
        )
    }

    private fun AppOpenAdConfig.toDefaultJson(): String {
        return json.encodeToString(
            serializer = AppOpenAdConfig.serializer(),
            value = this
        )
    }

    private fun readBannerAdConfig(): BannerAdConfig {
        val value = remoteConfig.getValue(
            RemoteConfigKeys.ADS_BANNER_CONFIG_REMOTE
        )

        val rawJson = value.asString()

        Log.d(TAG, "Banner RC key = ${RemoteConfigKeys.ADS_BANNER_CONFIG_REMOTE}")
        Log.d(TAG, "Banner RC source = ${value.source}")
        Log.d(TAG, "Banner RC raw JSON = $rawJson")

        if (rawJson.isBlank()) {
            Log.w(TAG, "Banner config JSON is blank. Using default config.")
            return defaultBannerAdConfig
        }

        return try {
            val parsedConfig = json.decodeFromString<BannerAdConfig>(rawJson)
            parsedConfig.sanitized()
        } catch (exception: Exception) {
            Log.e(TAG, "Failed to parse Banner config JSON. Using default config.", exception)
            defaultBannerAdConfig
        }
    }

    private fun BannerAdConfig.sanitized(): BannerAdConfig {
        val safePosition = when (collapsiblePosition.lowercase()) {
            BannerAdConfig.COLLAPSIBLE_TOP -> BannerAdConfig.COLLAPSIBLE_TOP
            BannerAdConfig.COLLAPSIBLE_BOTTOM -> BannerAdConfig.COLLAPSIBLE_BOTTOM
            else -> BannerAdConfig.COLLAPSIBLE_BOTTOM
        }

        return copy(
            adUnitId = adUnitId.ifBlank {
                BannerAdConfig.TEST_BANNER_AD_UNIT_ID
            },
            collapsiblePosition = safePosition
        )
    }

    private fun BannerAdConfig.toDefaultJson(): String {
        return json.encodeToString(
            serializer = BannerAdConfig.serializer(),
            value = this
        )
    }

    private fun readNativeAdConfig(): NativeAdConfig {
        val value = remoteConfig.getValue(
            RemoteConfigKeys.ADS_NATIVE_CONFIG_REMOTE
        )

        val rawJson = value.asString()

        Log.d(TAG, "Native RC key = ${RemoteConfigKeys.ADS_NATIVE_CONFIG_REMOTE}")
        Log.d(TAG, "Native RC source = ${value.source}")
        Log.d(TAG, "Native RC raw JSON = $rawJson")

        if (rawJson.isBlank()) {
            Log.w(TAG, "Native config JSON is blank. Using default config.")
            return defaultNativeAdConfig
        }

        return try {
            val parsedConfig = json.decodeFromString<NativeAdConfig>(rawJson)
            parsedConfig.sanitized()
        } catch (exception: SerializationException) {
            Log.e(TAG, "Failed to parse Native config JSON. Using default config.", exception)
            defaultNativeAdConfig
        } catch (exception: IllegalArgumentException) {
            Log.e(TAG, "Invalid Native config JSON. Using default config.", exception)
            defaultNativeAdConfig
        } catch (exception: Exception) {
            Log.e(TAG, "Unexpected Native config parse error. Using default config.", exception)
            defaultNativeAdConfig
        }
    }

    private fun NativeAdConfig.sanitized(): NativeAdConfig {
        return copy(
            adUnitId = adUnitId.ifBlank {
                NativeAdConfig.TEST_NATIVE_AD_UNIT_ID
            },
            maxAdCacheDurationMs = maxAdCacheDurationMs.coerceAtLeast(MIN_AD_CACHE_DURATION_MS),
            maxLoadRetryCount = maxLoadRetryCount.coerceIn(0, MAX_RETRY_COUNT),
            initialRetryDelayMs = initialRetryDelayMs.coerceAtLeast(MIN_INITIAL_RETRY_DELAY_MS),
            maxRetryDelayMs = maxRetryDelayMs.coerceAtLeast(MIN_MAX_RETRY_DELAY_MS),

            containerBackgroundColor = containerBackgroundColor.sanitizedHexColor("#FFFFFF"),
            containerBorderColor = containerBorderColor.sanitizedHexColor("#DADADA"),
            mediaBackgroundColor = mediaBackgroundColor.sanitizedHexColor("#F1F5F9"),

            headlineTextColor = headlineTextColor.sanitizedHexColor("#6F6F6F"),
            bodyTextColor = bodyTextColor.sanitizedHexColor("#8A8A8A"),

            ctaBackgroundColor = ctaBackgroundColor.sanitizedHexColor("#4285F4"),
            ctaTextColor = ctaTextColor.sanitizedHexColor("#FFFFFF"),

            adAttributionTextColor = adAttributionTextColor.sanitizedHexColor("#2E7D32"),
            adAttributionBackgroundColor = adAttributionBackgroundColor.sanitizedHexColor("#FFFFFF"),
            adAttributionBorderColor = adAttributionBorderColor.sanitizedHexColor("#2E7D32"),

            starRatingColor = starRatingColor.sanitizedHexColor("#8DE6DE"),

            cornerRadiusDp = cornerRadiusDp.coerceIn(0, 32),
            ctaCornerRadiusDp = ctaCornerRadiusDp.coerceIn(0, 32),
            adBadgeCornerRadiusDp = adBadgeCornerRadiusDp.coerceIn(0, 32),
            mediaCornerRadiusDp = mediaCornerRadiusDp.coerceIn(0, 32),
            containerBorderWidthDp = containerBorderWidthDp.coerceIn(0, 4),

            placements = placements.mapValues { entry ->
                entry.value.sanitized()
            }
        )
    }

    private fun NativeAdPlacementConfig.sanitized(): NativeAdPlacementConfig {
        return copy(
            insertAfterItemIndex = insertAfterItemIndex.coerceAtLeast(0),
            insertEveryNItems = insertEveryNItems.coerceAtLeast(0)
        )
    }


    private fun NativeAdConfig.toDefaultJson(): String {
        return json.encodeToString(
            serializer = NativeAdConfig.serializer(),
            value = this
        )
    }

    private fun String.sanitizedHexColor(
        fallback: String
    ): String {
        val value = trim()

        val isValidHexColor = Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$").matches(value)

        return if (isValidHexColor) {
            value
        } else {
            fallback
        }
    }

    private fun logAllRemoteConfigValues() {
        remoteConfig.all.forEach { (key, value) ->
            Log.d(TAG, "RemoteConfig: $key = ${value.asString()}")
        }
    }


    private suspend fun saveConfigToDataStore() {
        dataStore.edit { preferences ->
            // Save UX_Cam value
            val baseUrl = remoteConfig.getString(RemoteConfigKeys.BASE_URL_REMOTE)
            preferences[DataStoreKeys.BASE_API_URL] = baseUrl

            Log.d("RemoteConfigRepo", "baseUrl: $baseUrl")

            val secretKey = remoteConfig.getString(RemoteConfigKeys.API_SECRET_KEY_VALUE_REMOTE)
            preferences[DataStoreKeys.API_SECRET_KEY] = secretKey
            Log.d("RemoteConfigRepo", "secretKey: $secretKey")


            val isPremiumIconVisible = remoteConfig.getBoolean(RemoteConfigKeys.SHOW_PREMIUM_ICON_REMOTE)
            preferences[DataStoreKeys.SHOW_PREMIUM_ICON] = isPremiumIconVisible
            Log.d("RemoteConfigRepo", "isPremiumIconVisible: $isPremiumIconVisible")

        }
    }

    companion object {
        private const val TAG = "RemoteConfigRepo"

        private const val MIN_AD_CACHE_DURATION_MS = 60_000L
        private const val MAX_RETRY_COUNT = 10
        private const val MIN_INITIAL_RETRY_DELAY_MS = 500L
        private const val MIN_MAX_RETRY_DELAY_MS = 1_000L
    }
}