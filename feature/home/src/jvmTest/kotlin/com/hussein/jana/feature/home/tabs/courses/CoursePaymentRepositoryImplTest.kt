package com.hussein.jana.feature.home.tabs.courses

import com.hussein.jana.feature.home.tabs.courses.data.local.CourseCatalogStore
import com.hussein.jana.feature.home.tabs.courses.data.repository.CoursePaymentRepositoryImpl
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentMethod
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentStatus
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CoursePaymentRepositoryImplTest {

    private val store = CourseCatalogStore(listOf(subscribedCourse, lockedCourse))
    private val repository = CoursePaymentRepositoryImpl(store)

    @Test
    fun `createCheckout alone does not subscribe`() = runTest {
        val session = repository.createCheckout(lockedCourse.id, PaymentMethod.CARD).getOrThrow()

        assertEquals(lockedCourse.id, session.itemId)
        assertEquals(450.0, session.amount)
        assertEquals("EGP", session.currencyCode)
        assertFalse(store.find(lockedCourse.id)!!.isSubscribed)
    }

    @Test
    fun `verifyPayment of a created session subscribes the course`() = runTest {
        val session = repository.createCheckout(lockedCourse.id, PaymentMethod.FAWRY).getOrThrow()

        val verification = repository.verifyPayment(session.id).getOrThrow()

        assertEquals(PaymentStatus.SUCCESS, verification.status)
        assertTrue(verification.isSubscribed)
        assertTrue(store.find(lockedCourse.id)!!.isSubscribed)
    }

    @Test
    fun `verifyPayment of an unknown session fails and keeps the course locked`() = runTest {
        val verification = repository.verifyPayment("unknown").getOrThrow()

        assertEquals(PaymentStatus.FAILED, verification.status)
        assertFalse(verification.isSubscribed)
        assertFalse(store.find(lockedCourse.id)!!.isSubscribed)
    }
}
