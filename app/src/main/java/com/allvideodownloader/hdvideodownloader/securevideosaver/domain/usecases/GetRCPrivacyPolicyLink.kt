package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.RemoteConfigRepository

class GetRCPrivacyPolicyLink(
    private val repository: RemoteConfigRepository
) {
    suspend operator fun invoke(): String {
        return repository.getPrivacyPolicyLink()
    }
}