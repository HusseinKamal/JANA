package com.hussein.jana.feature.home.tabs.teacher.domain.model

import com.hussein.jana.feature.home.tabs.courses.domain.model.Money

enum class SubscriptionStatus { ACTIVE, EXPIRED, NOT_SUBSCRIBED }

data class TeacherSubscription(
    val planId: String,
    val planName: String,
    val status: SubscriptionStatus,
    val expiresAtMs: Long?,
    val price: Money?,
) {
    /** A missing price is never treated as free: without one the plan cannot be bought. */
    val isPurchasable: Boolean
        get() = status != SubscriptionStatus.ACTIVE && price != null
}
