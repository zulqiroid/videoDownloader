package com.app.videodownloader.domain.model

enum class SocialPlatform(val displayName: String) {
    FABKOOK("Fabkook"),
    INSTUDIO("Instudio"),
    TICKOTIK("TickoTik"),
    TWETSOT("Twetsot"),
    TALKTRAND("Talktrand"),
    DAILYMOOTS("DailyMoots"),
    PINTESTS("Pintests"),
    LIKETOO("Liketoo");

    companion object {
        fun fromName(name: String): SocialPlatform? {
            return entries.find {
                it.displayName.equals(name, ignoreCase = true)
            }
        }
    }
}