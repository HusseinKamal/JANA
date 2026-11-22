package com.hussein.jana.feature.home.tabs.courses.data.repository

import com.hussein.jana.feature.home.tabs.courses.data.local.CourseCatalogStore
import com.hussein.jana.feature.home.tabs.courses.domain.model.CheckoutSession
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentMethod
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentStatus
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentVerification
import com.hussein.jana.feature.home.tabs.courses.domain.repository.CoursePaymentRepository
import kotlinx.coroutines.delay

class CoursePaymentRepositoryImpl(
    private val store: CourseCatalogStore,
) : CoursePaymentRepository {

    private val sessions = mutableMapOf<String, CheckoutSession>()
    private var counter = 0

    // TODO: replace with real API call — POST /api/v1/courses/{id}/checkout
    //  (scoped to the active teacher platform via the authenticated session)
    override suspend fun createCheckout(
        courseId: String,
        method: PaymentMethod,
    ): Result<CheckoutSession> {
        delay(600)
        val course = store.find(courseId)
            ?: return Result.failure(NoSuchElementException("Course $courseId not found"))
        val price = course.price
            ?: return Result.failure(IllegalStateException("Course $courseId has no price"))
        val session = CheckoutSession(
            id = "chk_${courseId}_${counter++}",
            courseId = courseId,
            amount = price.amount,
            currencyCode = price.currencyCode,
            checkoutUrl = null,
        )
        sessions[session.id] = session
        return Result.success(session)
    }

    // TODO: replace with real API call — POST /api/v1/payments/{id}/verify
    //  The subscription is granted by the backend; the client only reflects the verified result.
    override suspend fun verifyPayment(checkoutId: String): Result<PaymentVerification> {
        delay(800)
        val session = sessions[checkoutId]
            ?: return Result.success(
                PaymentVerification(checkoutId, PaymentStatus.FAILED, isSubscribed = false),
            )
        store.markSubscribed(session.courseId)
        return Result.success(
            PaymentVerification(checkoutId, PaymentStatus.SUCCESS, isSubscribed = true),
        )
    }
}
