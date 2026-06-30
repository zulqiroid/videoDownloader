package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.RemoteConfigRepository

class GetPrivacyPolicyVisibility(
    private val repository: RemoteConfigRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.getPrivacyPolicyVisibility()
    }
}