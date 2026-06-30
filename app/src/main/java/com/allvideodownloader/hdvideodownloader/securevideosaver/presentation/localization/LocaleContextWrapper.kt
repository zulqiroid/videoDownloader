package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.localization

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

object LocaleContextWrapper {

    fun wrap(
        context: Context,
        language: AppLanguageCodes
    ): ContextWrapper {
        val locale = language.toLocale()

        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocales(LocaleList(locale))
        } else {
            @Suppress("DEPRECATION")
            configuration.locale = locale
        }

        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        val localizedContext = context.createConfigurationContext(configuration)

        return ContextWrapper(localizedContext)
    }
}