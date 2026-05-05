package com.app.videodownloader.domain.usecases

import android.util.Log
import com.app.videodownloader.domain.repository.RemoteConfigRepository

class GetRCPremiumIconVisibility(

    private val repository : RemoteConfigRepository
) {
    suspend operator fun invoke(): Boolean{


        val isPremiumIconVisible = repository.getPremiumIconVisibility()
        Log.d("GetRCPremiumIconVisibility", "isPremiumIconVisible: $isPremiumIconVisible")
        return isPremiumIconVisible
    }
}