package com.app.videodownloader.domain.model.billing

sealed interface BillingConnectionState {

    data object Idle : BillingConnectionState

    data object Connecting : BillingConnectionState

    data object Connected : BillingConnectionState

    data object Disconnected : BillingConnectionState

    data class Failed(
        val message: String,
    ) : BillingConnectionState
}