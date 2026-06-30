package com.allvideodownloader.hdvideodownloader.securevideosaver.core.appcheck

import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

class DefaultAppCheckInitializer : AppCheckInitializer {

    override fun initialize() {
        FirebaseAppCheck.getInstance()
            .installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
    }
}