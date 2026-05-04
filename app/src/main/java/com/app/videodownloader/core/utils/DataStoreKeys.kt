package com.app.videodownloader.core.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKeys {

    val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")

    val IS_POLICY_ACCEPTED = booleanPreferencesKey("is_policy_accepted")


    val DOWNLOAD_COMPLETE_ENABLED =
        booleanPreferencesKey("download_complete_notification_enabled")

    val DOWNLOAD_FAILED_ENABLED =
        booleanPreferencesKey("download_failed_notification_enabled")

    val APP_UPDATES_ENABLED =
        booleanPreferencesKey("app_updates_notification_enabled")



    val SELECTED_LANGUAGE_CODE =
        stringPreferencesKey("selected_language_code")
}