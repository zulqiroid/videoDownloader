package com.app.videodownloader.core.utils

import androidx.datastore.preferences.core.booleanPreferencesKey

object DataStoreKeys {

     val IS_FIRST_LAUNCH  = booleanPreferencesKey("is_first_launch")

    val IS_POLICY_ACCEPTED = booleanPreferencesKey("is_policy_accepted")

}