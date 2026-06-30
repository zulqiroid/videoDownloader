package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.localization

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import java.util.Locale

enum class AppLanguageCodes(
    val code: String,
    val localeTag: String,
    @StringRes val labelRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val flag: Int,
    val isRtl: Boolean = false
) {
    ENGLISH(
        code = "en",
        localeTag = "en",
        labelRes = R.string.language_english,
        descriptionRes = R.string.language_english_description,
        flag = R.drawable.english
    ),

    URDU(
        code = "ur",
        localeTag = "ur",
        labelRes = R.string.language_urdu,
        descriptionRes = R.string.language_urdu_description,
        flag = R.drawable.pakistan,
        isRtl = true
    ),

    HINDI(
        code = "hi",
        localeTag = "hi",
        labelRes = R.string.language_hindi,
        descriptionRes = R.string.language_hindi_description,
        flag = R.drawable.india
    ),

    ARABIC(
        code = "ar",
        localeTag = "ar",
        labelRes = R.string.language_arabic,
        descriptionRes = R.string.language_arabic_description,
        flag = R.drawable.saudi_arabia,
        isRtl = true
    ),

    SPANISH(
        code = "es",
        localeTag = "es",
        labelRes = R.string.language_spanish,
        descriptionRes = R.string.language_spanish_description,
        flag = R.drawable.spain
    ),

    FRENCH(
        code = "fr",
        localeTag = "fr",
        labelRes = R.string.language_french,
        descriptionRes = R.string.language_french_description,
        flag = R.drawable.france
    ),

    GERMAN(
        code = "de",
        localeTag = "de",
        labelRes = R.string.language_german,
        descriptionRes = R.string.language_german_description,
        flag = R.drawable.germany
    ),

    CHINESE(
        code = "zh",
        localeTag = "zh",
        labelRes = R.string.language_chinese,
        descriptionRes = R.string.language_chinese_description,
        flag = R.drawable.china
    );

    fun toLocale(): Locale {
        return Locale.forLanguageTag(localeTag)
    }

    companion object {
        val DEFAULT: AppLanguageCodes = ENGLISH

        fun fromCode(code: String?): AppLanguageCodes {
            if (code.isNullOrBlank()) return DEFAULT

            return entries.firstOrNull { language ->
                language.code.equals(code, ignoreCase = true) ||
                        language.localeTag.equals(code, ignoreCase = true)
            } ?: DEFAULT
        }

        fun fromLocale(locale: Locale?): AppLanguageCodes {
            if (locale == null) return DEFAULT

            return entries.firstOrNull { language ->
                language.code.equals(locale.language, ignoreCase = true)
            } ?: DEFAULT
        }
    }
}