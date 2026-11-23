package com.hussein.jana.feature.home.tabs.teacher.domain.repository

import com.hussein.jana.feature.home.tabs.courses.domain.model.CheckoutSession
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentMethod
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentVerification
import com.hussein.jana.feature.home.tabs.teacher.domain.model.TeacherSubscription

interface TeacherSubscriptionRepository {
    suspend fun getLiveLessonsSubscription(): Result<TeacherSubscription>
    suspend fun createLiveSubscriptionCheckout(
        planId: String,
        method: PaymentMethod,
    ): Result<CheckoutSession>
    suspend fun verifyLiveSubscriptionPayment(checkoutId: String): Result<PaymentVerification>
}
