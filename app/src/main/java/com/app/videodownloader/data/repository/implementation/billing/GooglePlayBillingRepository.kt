package com.app.videodownloader.data.repository.implementation.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.app.videodownloader.domain.model.billing.BillingConnectionState
import com.app.videodownloader.domain.model.billing.BillingPlan
import com.app.videodownloader.domain.model.billing.BillingPurchaseResult
import com.app.videodownloader.domain.model.billing.PremiumEntitlement
import com.app.videodownloader.domain.model.billing.PremiumEntitlementSource
import com.app.videodownloader.domain.model.billing.PremiumProduct
import com.app.videodownloader.domain.repository.billing.BillingRepository
import com.app.videodownloader.domain.repository.billing.PremiumEntitlementRepository
import com.app.videodownloader.presentation.screens.premium.state.PlanType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GooglePlayBillingRepository(
    context: Context,
    private val premiumEntitlementRepository: PremiumEntitlementRepository,
) : BillingRepository {

    private val appContext = context.applicationContext

    private val repositoryScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )

    private val _connectionState = MutableStateFlow<BillingConnectionState>(
        BillingConnectionState.Idle
    )
    override val connectionState: StateFlow<BillingConnectionState> = _connectionState

    private val _plans = MutableStateFlow<List<BillingPlan>>(emptyList())
    override val plans: StateFlow<List<BillingPlan>> = _plans

    private val purchaseResultChannel = Channel<BillingPurchaseResult>(Channel.BUFFERED)
    override val purchaseResults = purchaseResultChannel.receiveAsFlow()

    private val productDetailsByProductId = mutableMapOf<String, ProductDetails>()

    @Volatile
    private var isConnecting: Boolean = false

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        handlePurchasesUpdated(
            billingResult = billingResult,
            purchases = purchases.orEmpty()
        )
    }

    private val billingClient: BillingClient = BillingClient
        .newBuilder(appContext)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(
            PendingPurchasesParams
                .newBuilder()
                .enableOneTimeProducts()
                .enablePrepaidPlans()
                .build()
        )
        .build()

    override fun connect() {
        if (billingClient.isReady) {
            _connectionState.value = BillingConnectionState.Connected
            queryProducts()
            restorePurchases()
            return
        }

        if (isConnecting) return

        isConnecting = true
        _connectionState.value = BillingConnectionState.Connecting

        billingClient.startConnection(
            object : BillingClientStateListener {

                override fun onBillingSetupFinished(
                    billingResult: BillingResult,
                ) {
                    isConnecting = false

                    if (billingResult.responseCode == BillingResponseCode.OK) {
                        Log.d(TAG, "Billing connected.")

                        _connectionState.value = BillingConnectionState.Connected

                        queryProducts()
                        restorePurchases()
                    } else {
                        val message = billingResult.safeMessage(
                            fallback = "Unable to connect to Google Play Billing"
                        )

                        Log.e(TAG, "Billing setup failed: $message")

                        _connectionState.value = BillingConnectionState.Failed(message)
                    }
                }

                override fun onBillingServiceDisconnected() {
                    isConnecting = false

                    Log.d(TAG, "Billing service disconnected.")

                    _connectionState.value = BillingConnectionState.Disconnected
                }
            }
        )
    }

    override fun disconnect() {
        if (billingClient.isReady) {
            billingClient.endConnection()
        }

        isConnecting = false
        _connectionState.value = BillingConnectionState.Disconnected
    }

    override fun queryProducts() {
        if (!billingClient.isReady) {
            connect()
            return
        }

        productDetailsByProductId.clear()

        querySubscriptionProducts()
        queryInAppProducts()
    }

    override fun launchPurchase(
        activity: Activity,
        planType: PlanType,
    ) {
        if (activity.isFinishing || activity.isDestroyed) {
            emitPurchaseResult(
                BillingPurchaseResult.Error("Unable to start purchase. Activity is not valid.")
            )
            return
        }

        if (!billingClient.isReady) {
            emitPurchaseResult(
                BillingPurchaseResult.Error("Billing is not connected yet.")
            )
            connect()
            return
        }

        val premiumProduct = PremiumProduct.fromPlanType(planType)

        val productDetails = productDetailsByProductId[premiumProduct.productId]

        if (productDetails == null) {
            emitPurchaseResult(
                BillingPurchaseResult.Error("Selected plan is not available right now.")
            )
            queryProducts()
            return
        }

        val productDetailsParams = buildProductDetailsParams(
            premiumProduct = premiumProduct,
            productDetails = productDetails
        ) ?: return

        val billingFlowParams = BillingFlowParams
            .newBuilder()
            .setProductDetailsParamsList(
                listOf(productDetailsParams)
            )
            .build()

        val billingResult = billingClient.launchBillingFlow(
            activity,
            billingFlowParams
        )

        when (billingResult.responseCode) {
            BillingResponseCode.OK -> {
                Log.d(TAG, "Billing flow launched for ${premiumProduct.productId}")
            }

            BillingResponseCode.ITEM_ALREADY_OWNED -> {
                emitPurchaseResult(BillingPurchaseResult.AlreadyOwned)
                restorePurchases()
            }

            else -> {
                emitPurchaseResult(
                    BillingPurchaseResult.Error(
                        billingResult.safeMessage("Unable to start purchase.")
                    )
                )
            }
        }
    }

    override fun restorePurchases() {
        if (!billingClient.isReady) {
            connect()
            return
        }

        queryOwnedPurchases(
            productType = BillingClient.ProductType.SUBS
        )

        queryOwnedPurchases(
            productType = BillingClient.ProductType.INAPP
        )
    }

    private fun querySubscriptionProducts() {
        val products = PremiumProduct
            .subscriptions()
            .map { product ->
                QueryProductDetailsParams.Product
                    .newBuilder()
                    .setProductId(product.productId)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            }

        if (products.isEmpty()) return

        val params = QueryProductDetailsParams
            .newBuilder()
            .setProductList(products)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, result ->
            handleProductDetailsResult(
                billingResult = billingResult,
                productDetailsList = result.productDetailsList
            )
        }
    }

    private fun queryInAppProducts() {
        val products = PremiumProduct
            .inApps()
            .map { product ->
                QueryProductDetailsParams.Product
                    .newBuilder()
                    .setProductId(product.productId)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            }

        if (products.isEmpty()) return

        val params = QueryProductDetailsParams
            .newBuilder()
            .setProductList(products)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, result ->
            handleProductDetailsResult(
                billingResult = billingResult,
                productDetailsList = result.productDetailsList
            )
        }
    }

    private fun handleProductDetailsResult(
        billingResult: BillingResult,
        productDetailsList: List<ProductDetails>,
    ) {
        if (billingResult.responseCode != BillingResponseCode.OK) {
            val message = billingResult.safeMessage("Unable to load premium plans.")

            Log.e(TAG, "Product query failed: $message")

            emitPurchaseResult(
                BillingPurchaseResult.Error(message)
            )
            return
        }

        productDetailsList.forEach { productDetails ->
            productDetailsByProductId[productDetails.productId] = productDetails
        }

        publishBillingPlans()
    }

    private fun publishBillingPlans() {
        val billingPlans = PremiumProduct.entries.mapNotNull { premiumProduct ->
            val productDetails = productDetailsByProductId[premiumProduct.productId]
                ?: return@mapNotNull null

            BillingPlan(
                productId = premiumProduct.productId,
                planType = premiumProduct.planType,
                titleRes = premiumProduct.planType.titleRes,
                badgeRes = premiumProduct.planType.badgeRes,
                formattedPrice = productDetails.displayPriceFor(
                    productType = premiumProduct.productType
                ),
                productType = premiumProduct.productType,
                offerToken = productDetails.firstOfferTokenOrNull(
                    productType = premiumProduct.productType
                ),
                isAvailable = true
            )
        }

        _plans.value = billingPlans

        Log.d(TAG, "Billing plans published: $billingPlans")
    }

    private fun buildProductDetailsParams(
        premiumProduct: PremiumProduct,
        productDetails: ProductDetails,
    ): BillingFlowParams.ProductDetailsParams? {
        val builder = BillingFlowParams
            .ProductDetailsParams
            .newBuilder()
            .setProductDetails(productDetails)

        if (premiumProduct.productType == BillingClient.ProductType.SUBS) {
            val offerToken = productDetails.firstOfferTokenOrNull(
                productType = premiumProduct.productType
            )

            if (offerToken.isNullOrBlank()) {
                emitPurchaseResult(
                    BillingPurchaseResult.Error("Subscription offer is not available.")
                )
                return null
            }

            builder.setOfferToken(offerToken)
        }

        return builder.build()
    }

    private fun queryOwnedPurchases(
        productType: String,
    ) {
        val params = QueryPurchasesParams
            .newBuilder()
            .setProductType(productType)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode != BillingResponseCode.OK) {
                Log.e(
                    TAG,
                    "Restore failed for $productType: ${billingResult.debugMessage}"
                )
                return@queryPurchasesAsync
            }

            handlePurchases(
                purchases = purchases,
                fromRestore = true
            )
        }
    }

    private fun handlePurchasesUpdated(
        billingResult: BillingResult,
        purchases: List<Purchase>,
    ) {
        when (billingResult.responseCode) {
            BillingResponseCode.OK -> {
                handlePurchases(
                    purchases = purchases,
                    fromRestore = false
                )
            }

            BillingResponseCode.USER_CANCELED -> {
                emitPurchaseResult(BillingPurchaseResult.Cancelled)
            }

            BillingResponseCode.ITEM_ALREADY_OWNED -> {
                emitPurchaseResult(BillingPurchaseResult.AlreadyOwned)
                restorePurchases()
            }

            else -> {
                emitPurchaseResult(
                    BillingPurchaseResult.Error(
                        billingResult.safeMessage("Purchase failed.")
                    )
                )
            }
        }
    }

    private fun handlePurchases(
        purchases: List<Purchase>,
        fromRestore: Boolean,
    ) {
        if (purchases.isEmpty()) {
            if (fromRestore) {
                clearPremiumEntitlement()
            }
            return
        }

        val purchasedPremiumProductIds = mutableSetOf<String>()
        var hasPendingPremiumPurchase = false

        purchases.forEach { purchase ->
            val premiumProductIds = purchase.products.filter { productId ->
                PremiumProduct.fromProductId(productId) != null
            }

            if (premiumProductIds.isEmpty()) return@forEach

            when (purchase.purchaseState) {
                Purchase.PurchaseState.PURCHASED -> {
                    purchasedPremiumProductIds.addAll(premiumProductIds)
                    acknowledgePurchaseIfNeeded(purchase)
                }

                Purchase.PurchaseState.PENDING -> {
                    hasPendingPremiumPurchase = true
                }

                else -> Unit
            }
        }

        if (purchasedPremiumProductIds.isNotEmpty()) {
            savePremiumEntitlement(
                activeProductIds = purchasedPremiumProductIds,
                source = if (fromRestore) {
                    PremiumEntitlementSource.RestoredPurchase
                } else {
                    PremiumEntitlementSource.GooglePlayPurchase
                }
            )

            if (!fromRestore) {
                emitPurchaseResult(BillingPurchaseResult.Success)
            }

            return
        }

        if (hasPendingPremiumPurchase && !fromRestore) {
            emitPurchaseResult(BillingPurchaseResult.Pending)
            return
        }

        if (fromRestore) {
            clearPremiumEntitlement()
        }
    }

    private fun acknowledgePurchaseIfNeeded(
        purchase: Purchase,
    ) {
        if (purchase.isAcknowledged) {
            Log.d(TAG, "Purchase already acknowledged.")
            return
        }

        val params = AcknowledgePurchaseParams
            .newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.acknowledgePurchase(params) { billingResult ->
            if (billingResult.responseCode == BillingResponseCode.OK) {
                Log.d(TAG, "Purchase acknowledged successfully.")
            } else {
                Log.e(
                    TAG,
                    "Purchase acknowledge failed: ${billingResult.debugMessage}"
                )

                emitPurchaseResult(
                    BillingPurchaseResult.Error(
                        billingResult.safeMessage("Purchase acknowledgement failed.")
                    )
                )
            }
        }
    }

    private fun ProductDetails.displayPriceFor(
        productType: String,
    ): String {
        return when (productType) {
            BillingClient.ProductType.SUBS -> {
                subscriptionOfferDetails
                    ?.firstOrNull()
                    ?.pricingPhases
                    ?.pricingPhaseList
                    ?.firstOrNull()
                    ?.formattedPrice
                    ?: "--"
            }

            BillingClient.ProductType.INAPP -> {
                oneTimePurchaseOfferDetails
                    ?.formattedPrice
                    ?: "--"
            }

            else -> "--"
        }
    }

    private fun ProductDetails.firstOfferTokenOrNull(
        productType: String,
    ): String? {
        return if (productType == BillingClient.ProductType.SUBS) {
            subscriptionOfferDetails
                ?.firstOrNull()
                ?.offerToken
        } else {
            null
        }
    }

    private fun BillingResult.safeMessage(
        fallback: String,
    ): String {
        return debugMessage.takeIf { it.isNotBlank() } ?: fallback
    }

    private fun emitPurchaseResult(
        result: BillingPurchaseResult,
    ) {
        repositoryScope.launch {
            purchaseResultChannel.send(result)
        }
    }

    private fun savePremiumEntitlement(
        activeProductIds: Set<String>,
        source: PremiumEntitlementSource,
    ) {
        repositoryScope.launch {
            premiumEntitlementRepository.updateEntitlement(
                PremiumEntitlement(
                    isPremium = activeProductIds.isNotEmpty(),
                    activeProductIds = activeProductIds,
                    source = source,
                    updatedAtMillis = System.currentTimeMillis()
                )
            )
        }
    }

    private fun clearPremiumEntitlement() {
        repositoryScope.launch {
            premiumEntitlementRepository.clearEntitlement()
        }
    }

    companion object {
        private const val TAG = "GooglePlayBilling"
    }
}