package com.app.videodownloader.domain.model.ads

enum class BannerAdScreen(
    val remoteKey: String
) {
    Home("home"),
    Player("player"),
    Download("download"),
    Reels("reels"),
    More("more"),
    Social("social"),
    MediaPlayer("media_player"),

    AppLanguage("app_language"),

    OnBoarding("onboarding"),
}

enum class BannerAdSlot {
    Top,
    Bottom
}