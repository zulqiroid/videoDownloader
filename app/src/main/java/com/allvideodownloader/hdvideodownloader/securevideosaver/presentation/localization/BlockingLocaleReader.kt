package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.localization

import android.content.Context
import com.allvideodownloader.hdvideodownloader.securevideosaver.core.utils.DataStoreKeys
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.local.dataStore.dataStore
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