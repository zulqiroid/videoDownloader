package com.app.videodownloader.domain.model.ads

import kotlinx.serialization.Serializable

@Serializable
enum class NativeAdListInsertionMode {
    Start,
    End,
    AfterItem,
    EveryNItems,
    Disabled
}