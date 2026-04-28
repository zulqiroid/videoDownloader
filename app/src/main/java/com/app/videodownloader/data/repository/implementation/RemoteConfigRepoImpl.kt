package com.app.videodownloader.data.repository.implementation
import android.util.Log
import com.app.videodownloader.core.utils.RemoteConfigKeys
import com.app.videodownloader.domain.model.ApiKey
import com.app.videodownloader.domain.repository.RemoteConfigRepository
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RemoteConfigRepoImpl: RemoteConfigRepository {
    private val remoteConfig: FirebaseRemoteConfig by lazy { Firebase.remoteConfig }
    companion object {
        private const val TAG = "RemoteConfigRepo"
    }

    override suspend fun initializeAndFetch(onComplete: () -> Unit) {
        Log.d(TAG, "Initializing Remote Config")

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0 // For development, use 3600 for production3
            fetchTimeoutInSeconds = 60
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
            .addOnCompleteListener { settingsTask ->
                if (settingsTask.isSuccessful) {
                    Log.d(TAG, "Remote config settings applied successfully")

                    remoteConfig.fetchAndActivate()
                        .addOnCompleteListener { fetchTask ->
                            if (fetchTask.isSuccessful) {
                                Log.d(
                                    TAG,
                                    "Remote config fetched and activated: ${fetchTask.result}"
                                )
                                logAllRemoteConfigValues()

                                // Save all values to DataStore
                                CoroutineScope(Dispatchers.IO).launch {
                                    onComplete()
                                }
                            } else {
                                Log.e(TAG, "Remote config fetch failed", fetchTask.exception)
                                onComplete()
                            }
                        }
                } else {
                    Log.e(TAG, "Remote config settings failed", settingsTask.exception)
                    onComplete()
                }
            }
    }

    override suspend fun getApiSecretKey(): ApiKey {
        return ApiKey(
            remoteConfig.getString(RemoteConfigKeys.API_SECRET_KEY_REMOTE),
            remoteConfig.getString(RemoteConfigKeys.API_SECRET_KEY_value_REMOTE)
        )
    }

    override suspend fun getBaseUrl(): String {
        return remoteConfig.getString(RemoteConfigKeys.BASE_URL_REMOTE)
    }

    private fun logAllRemoteConfigValues() {
        remoteConfig.all.forEach { (key, value) ->
            Log.d(TAG, "RemoteConfig: $key = ${value.asString()}")
        }
    }

}