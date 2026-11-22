package com.hussein.jana.feature.home.tabs.courses.domain.usecase

import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentVerification
import com.hussein.jana.feature.home.tabs.courses.domain.repository.CoursePaymentRepository

class VerifyCoursePaymentUseCase(private val repository: CoursePaymentRepository) {
    suspend operator fun invoke(checkoutId: String): Result<PaymentVerification> =
        repository.verifyPayment(checkoutId)
}
