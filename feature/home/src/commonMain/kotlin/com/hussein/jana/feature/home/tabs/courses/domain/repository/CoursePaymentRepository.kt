package com.hussein.jana.feature.home.tabs.courses.domain.repository

import com.hussein.jana.feature.home.tabs.courses.domain.model.CheckoutSession
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentMethod
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentVerification

interface CoursePaymentRepository {
    suspend fun createCheckout(courseId: String, method: PaymentMethod): Result<CheckoutSession>
    suspend fun verifyPayment(checkoutId: String): Result<PaymentVerification>
}
