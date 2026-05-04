package com.app.videodownloader.presentation.localization

import android.content.Context
import com.app.videodownloader.core.utils.DataStoreKeys
import com.app.videodownloader.data.local.dataStore.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object BlockingLocaleReader {

    fun readLanguage(context: Context): AppLanguageCodes {
        return runCatching {
            runBlocking {
                val preferences = context.dataStore.data.first()
                val savedCode = preferences[DataStoreKeys.SELECTED_LANGUAGE_CODE]
                AppLanguageCodes.fromCode(savedCode)
            }
        }.getOrDefault(AppLanguageCodes.DEFAULT)
    }
}