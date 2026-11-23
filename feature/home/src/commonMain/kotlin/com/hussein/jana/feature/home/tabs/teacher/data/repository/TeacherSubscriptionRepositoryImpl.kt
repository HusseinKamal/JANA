package com.hussein.jana.feature.home.tabs.teacher.data.repository

import com.hussein.jana.feature.home.tabs.courses.domain.model.CheckoutSession
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentMethod
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentStatus
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentVerification
import com.hussein.jana.feature.home.tabs.teacher.data.mock.MockTeacherData
import com.hussein.jana.feature.home.tabs.teacher.domain.model.SubscriptionStatus
import com.hussein.jana.feature.home.tabs.teacher.domain.model.TeacherSubscription
import com.hussein.jana.feature.home.tabs.teacher.domain.repository.TeacherSubscriptionRepository
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class TeacherSubscriptionRepositoryImpl : TeacherSubscriptionRepository {

    private var subscription: TeacherSubscription = MockTeacherData.liveSubscription
    private val sessions = mutableMapOf<String, CheckoutSession>()
    private var counter = 0

    // TODO: replace with real API call — GET /api/v1/me/subscriptions
    override suspend fun getLiveLessonsSubscription(): Result<TeacherSubscription> {
        delay(400)
        return Result.success(subscription)
    }

    // TODO: replace with real API call — GET /api/v1/subscriptions/plans
    //  and POST /api/v1/subscriptions/{planId}/checkout
    override suspend fun createLiveSubscriptionCheckout(
        planId: String,
        method: PaymentMethod,
    ): Result<CheckoutSession> {
        delay(600)
        if (planId != subscription.planId) {
            return Result.failure(NoSuchElementException("Plan $planId not found"))
        }
        val price = subscription.price
            ?: return Result.failure(IllegalStateException("Plan $planId has no price"))
        val session = CheckoutSession(
            id = "chk_${planId}_${counter++}",
            itemId = planId,
            amount = price.amount,
            currencyCode = price.currencyCode,
            checkoutUrl = null,
        )
        sessions[session.id] = session
        return Result.success(session)
    }

    // TODO: replace with real API call — POST /api/v1/payments/{id}/verify
    //  The subscription is granted by the backend; this is the only place it becomes ACTIVE.
    @OptIn(ExperimentalTime::class)
    override suspend fun verifyLiveSubscriptionPayment(checkoutId: String): Result<PaymentVerification> {
        delay(800)
        sessions[checkoutId]
            ?: return Result.success(
                PaymentVerification(checkoutId, PaymentStatus.FAILED, isSubscribed = false),
            )
        val nowMs = Clock.System.now().toEpochMilliseconds()
        subscription = subscription.copy(
            status = SubscriptionStatus.ACTIVE,
            expiresAtMs = nowMs + SUBSCRIPTION_DURATION_MS,
        )
        return Result.success(
            PaymentVerification(checkoutId, PaymentStatus.SUCCESS, isSubscribed = true),
        )
    }

    private companion object {
        const val SUBSCRIPTION_DURATION_MS = 30L * 24 * 60 * 60 * 1000
    }
}
