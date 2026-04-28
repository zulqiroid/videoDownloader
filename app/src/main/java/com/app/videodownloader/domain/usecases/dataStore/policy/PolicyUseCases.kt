package com.app.videodownloader.domain.usecases.dataStore.policy

import com.app.videodownloader.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow

data class PolicyUseCases(
    val getPolicyAcceptedUseCase: GetPolicyAcceptedUseCase,
    val setPolicyAcceptedUseCase: SetPolicyAcceptedUseCase
)


class GetPolicyAcceptedUseCase(
    private val repository: DataStoreRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.isPolicyAccepted
    }
}


class SetPolicyAcceptedUseCase(
    private val repository: DataStoreRepository
) {
    suspend operator fun invoke(value: Boolean) {
        repository.setPolicyAccepted(value)
    }
}
