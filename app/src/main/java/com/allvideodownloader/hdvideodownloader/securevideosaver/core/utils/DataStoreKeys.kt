package com.allvideodownloader.hdvideodownloader.securevideosaver.core.utils

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

    val BASE_API_URL= stringPreferencesKey("base_api_url")

    val API_SECRET_KEY = stringPreferencesKey("api_secret_key")

    val SHOW_PREMIUM_ICON = booleanPreferencesKey("show_premium_icon")

    val SHOW_PRIVACY_POLICY = booleanPreferencesKey("show_privacy_policy")
    val PRIVACY_POLICY_LINK = stringPreferencesKey("privacy_policy_link")



    val SELECTED_LANGUAGE_CODE =
        stringPreferencesKey("selected_language_code")


    val IS_PREMIUM_USER =
        booleanPreferencesKey("is_premium_user")

    val PREMIUM_ACTIVE_PRODUCT_IDS =
        stringPreferencesKey("premium_active_product_ids")

    val PREMIUM_ENTITLEMENT_SOURCE =
        stringPreferencesKey("premium_entitlement_source")

    val PREMIUM_ENTITLEMENT_UPDATED_AT =
        stringPreferencesKey("premium_entitlement_updated_at")


    val FCM_TOKEN = stringPreferencesKey("fcm_token")
    val IS_TOKEN_SYNCED = booleanPreferencesKey("is_fcm_token_synced")
}