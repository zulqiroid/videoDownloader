package com.app.videodownloader.domain.usecases.ads

import android.app.Activity
import com.app.videodownloader.domain.model.ads.AdsConsentResult
import com.app.videodownloader.domain.repository.ads.AdsConsentRepository

data class AdsConsentUseCases(
    val requestAdsConsentUseCase: RequestAdsConsentUseCase,
    val showPrivacyOptionsFormUseCase: ShowPrivacyOptionsFormUseCase,
    val canRequestAdsUseCase: CanRequestAdsUseCase,
    val isPrivacyOptionsRequiredUseCase: IsPrivacyOptionsRequiredUseCase,
    val resetAdsConsentForTestingUseCase: ResetAdsConsentForTestingUseCase
)

class RequestAdsConsentUseCase(
    private val repository: AdsConsentRepository
) {
    suspend operator fun invoke(
        activity: Activity
    ): AdsConsentResult {
        return repository.requestConsent(activity)
    }
}

class ShowPrivacyOptionsFormUseCase(
    private val repository: AdsConsentRepository
) {
    suspend operator fun invoke(
        activity: Activity
    ): AdsConsentResult {
        return repository.showPrivacyOptionsForm(activity)
    }
}

class CanRequestAdsUseCase(
    private val repository: AdsConsentRepository
) {
    operator fun invoke(): Boolean {
        return repository.canRequestAds()
    }
}

class IsPrivacyOptionsRequiredUseCase(
    private val repository: AdsConsentRepository
) {
    operator fun invoke(): Boolean {
        return repository.isPrivacyOptionsRequired()
    }
}

class ResetAdsConsentForTestingUseCase(
    private val repository: AdsConsentRepository
) {
    operator fun invoke() {
        repository.resetConsentForTesting()
    }
}