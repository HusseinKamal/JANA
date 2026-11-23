package com.hussein.jana.feature.home.tabs.teacher.domain.usecase

import com.hussein.jana.feature.home.tabs.courses.domain.model.CheckoutSession
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentMethod
import com.hussein.jana.feature.home.tabs.teacher.domain.repository.TeacherSubscriptionRepository

class CreateLiveSubscriptionCheckoutUseCase(private val repository: TeacherSubscriptionRepository) {
    suspend operator fun invoke(planId: String, method: PaymentMethod): Result<CheckoutSession> =
        repository.createLiveSubscriptionCheckout(planId, method)
}
