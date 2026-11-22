package com.hussein.jana.feature.home.tabs.courses.domain.model

enum class PaymentMethod { CARD, WALLET, FAWRY }

enum class PaymentStatus { SUCCESS, PENDING, FAILED }

data class CheckoutSession(
    val id: String,
    val courseId: String,
    val amount: Double,
    val currencyCode: String,
    /** Hosted payment page, when the gateway requires a redirect. */
    val checkoutUrl: String? = null,
)

data class PaymentVerification(
    val checkoutId: String,
    val status: PaymentStatus,
    val isSubscribed: Boolean,
)
