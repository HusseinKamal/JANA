package com.hussein.jana.feature.home.tabs.courses.domain.usecase

import com.hussein.jana.feature.home.tabs.courses.domain.model.CheckoutSession
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentMethod
import com.hussein.jana.feature.home.tabs.courses.domain.repository.CoursePaymentRepository

class CreateCourseCheckoutUseCase(private val repository: CoursePaymentRepository) {
    suspend operator fun invoke(courseId: String, method: PaymentMethod): Result<CheckoutSession> =
        repository.createCheckout(courseId, method)
}
