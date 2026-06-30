package com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.billing

 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.billing.PremiumAccessController
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.billing.ObserveIsPremiumUserUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class PremiumAccessControllerImpl(
    observeIsPremiumUserUseCase: ObserveIsPremiumUserUseCase,
    applicationScope: CoroutineScope,
) : PremiumAccessController {

    private val _isPremiumUser = MutableStateFlow(false)
    override val isPremiumUser: StateFlow<Boolean> = _isPremiumUser

    init {
        applicationScope.launch {
            observeIsPremiumUserUseCase()
                .distinctUntilChanged()
                .collect { isPremium ->
                    _isPremiumUser.value = isPremium
                }
        }
    }

    override fun isPremium(): Boolean {
        return _isPremiumUser.value
    }
}