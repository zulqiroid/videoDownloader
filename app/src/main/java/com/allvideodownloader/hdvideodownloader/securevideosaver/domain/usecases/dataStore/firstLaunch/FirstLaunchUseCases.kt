package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.firstLaunch

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow


class GetFirstLaunchUseCase(
    private val repository: DataStoreRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.isFirstLaunch
    }
}


class SetFirstLaunchUseCase(
    private val repository: DataStoreRepository
) {
    suspend operator fun invoke(value: Boolean) {
        repository.setFirstLaunch(value)
    }
}

data class FirstLaunchUseCases(
    val getFirstLaunch: GetFirstLaunchUseCase,
    val setFirstLaunch: SetFirstLaunchUseCase
)

