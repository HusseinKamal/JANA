package com.hussein.jana.feature.home.tabs.teacher.domain.usecase

import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentVerification
import com.hussein.jana.feature.home.tabs.teacher.domain.repository.TeacherSubscriptionRepository

class VerifyLiveSubscriptionPaymentUseCase(private val repository: TeacherSubscriptionRepository) {
    suspend operator fun invoke(checkoutId: String): Result<PaymentVerification> =
        repository.verifyLiveSubscriptionPayment(checkoutId)
}
