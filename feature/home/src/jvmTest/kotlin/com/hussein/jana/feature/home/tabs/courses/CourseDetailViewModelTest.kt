package com.hussein.jana.feature.home.tabs.courses

import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentMethod
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentStatus
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.CreateCourseCheckoutUseCase
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.GetCourseDetailUseCase
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.GetCourseUnitsUseCase
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.VerifyCoursePaymentUseCase
import com.hussein.jana.feature.home.tabs.courses.presentation.detail.CheckoutStatus
import com.hussein.jana.feature.home.tabs.courses.presentation.detail.CourseDetailEffect
import com.hussein.jana.feature.home.tabs.courses.presentation.detail.CourseDetailIntent
import com.hussein.jana.feature.home.tabs.courses.presentation.detail.CourseDetailViewModel
import com.hussein.jana.feature.home.tabs.courses.presentation.detail.PaymentFailure
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CourseDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var courseRepository: FakeCourseRepository
    private lateinit var paymentRepository: FakeCoursePaymentRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        courseRepository = FakeCourseRepository()
        paymentRepository = FakeCoursePaymentRepository(courseRepository)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(courseId: String, openCheckout: Boolean = false) = CourseDetailViewModel(
        courseId = courseId,
        openCheckout = openCheckout,
        getCourseDetail = GetCourseDetailUseCase(courseRepository),
        getCourseUnits = GetCourseUnitsUseCase(courseRepository),
        createCourseCheckout = CreateCourseCheckoutUseCase(paymentRepository),
        verifyCoursePayment = VerifyCoursePaymentUseCase(paymentRepository),
    )

    @Test
    fun `locked course is locked and lessons show locked message`() = runTest(testDispatcher) {
        val vm = buildViewModel(lockedCourse.id)
        advanceUntilIdle()

        assertTrue(vm.state.value.isLocked)
        vm.onIntent(CourseDetailIntent.OpenLesson(fakeUnits.first().lessons.first()))
        assertEquals(CourseDetailEffect.ShowLessonLocked, vm.effect.first())
    }

    @Test
    fun `subscribed course is unlocked and lessons are not blocked`() = runTest(testDispatcher) {
        val vm = buildViewModel(subscribedCourse.id)
        advanceUntilIdle()

        assertFalse(vm.state.value.isLocked)
        vm.onIntent(CourseDetailIntent.OpenLesson(fakeUnits.first().lessons.first()))
        assertEquals(CourseDetailEffect.ShowLessonComingSoon, vm.effect.first())
    }

    @Test
    fun `openCheckout auto-opens the sheet only for locked courses`() = runTest(testDispatcher) {
        val locked = buildViewModel(lockedCourse.id, openCheckout = true)
        val subscribed = buildViewModel(subscribedCourse.id, openCheckout = true)
        advanceUntilIdle()

        assertEquals(CheckoutStatus.SELECTING_METHOD, locked.state.value.checkout.status)
        assertEquals(CheckoutStatus.IDLE, subscribed.state.value.checkout.status)
    }

    @Test
    fun `subscribe success unlocks only after verification completes`() = runTest(testDispatcher) {
        val gate = CompletableDeferred<Unit>()
        paymentRepository.verifyGate = gate
        val vm = buildViewModel(lockedCourse.id)
        advanceUntilIdle()

        vm.onIntent(CourseDetailIntent.SubscribeClicked)
        vm.onIntent(CourseDetailIntent.PaymentMethodSelected(PaymentMethod.WALLET))
        vm.onIntent(CourseDetailIntent.PayClicked)
        assertEquals(CheckoutStatus.PROCESSING, vm.state.value.checkout.status)
        assertTrue(vm.state.value.isLocked)

        advanceUntilIdle()
        assertEquals(CheckoutStatus.VERIFYING, vm.state.value.checkout.status)
        assertTrue(vm.state.value.isLocked, "Course must stay locked until the backend verifies")
        assertFalse(courseRepository.courses.value.first { it.id == lockedCourse.id }.isSubscribed)

        gate.complete(Unit)
        advanceUntilIdle()

        assertEquals(CheckoutStatus.SUCCESS, vm.state.value.checkout.status)
        assertFalse(vm.state.value.checkout.isVisible)
        assertFalse(vm.state.value.isLocked)
        assertEquals(CourseDetailEffect.SubscriptionActivated, vm.effect.first())
    }

    @Test
    fun `dismiss is ignored while payment is busy`() = runTest(testDispatcher) {
        paymentRepository.verifyGate = CompletableDeferred()
        val vm = buildViewModel(lockedCourse.id)
        advanceUntilIdle()

        vm.onIntent(CourseDetailIntent.SubscribeClicked)
        vm.onIntent(CourseDetailIntent.PayClicked)
        vm.onIntent(CourseDetailIntent.DismissCheckout)
        assertEquals(CheckoutStatus.PROCESSING, vm.state.value.checkout.status)

        advanceUntilIdle()
        vm.onIntent(CourseDetailIntent.DismissCheckout)
        assertEquals(CheckoutStatus.VERIFYING, vm.state.value.checkout.status)
    }

    @Test
    fun `verify failure keeps course locked and retry succeeds with a new checkout`() = runTest(testDispatcher) {
        paymentRepository.verifyResults += PaymentStatus.FAILED
        val vm = buildViewModel(lockedCourse.id)
        advanceUntilIdle()

        vm.onIntent(CourseDetailIntent.SubscribeClicked)
        vm.onIntent(CourseDetailIntent.PayClicked)
        advanceUntilIdle()

        assertEquals(CheckoutStatus.FAILED, vm.state.value.checkout.status)
        assertEquals(PaymentFailure.FAILED, vm.state.value.checkout.failure)
        assertTrue(vm.state.value.checkout.isVisible)
        assertTrue(vm.state.value.isLocked)
        assertFalse(courseRepository.courses.value.first { it.id == lockedCourse.id }.isSubscribed)

        vm.onIntent(CourseDetailIntent.RetryPayment)
        advanceUntilIdle()

        assertEquals(CheckoutStatus.SUCCESS, vm.state.value.checkout.status)
        assertFalse(vm.state.value.isLocked)
        assertEquals(2, paymentRepository.createdSessions.size)
    }

    @Test
    fun `pending verification keeps course locked and retry re-verifies the same session`() =
        runTest(testDispatcher) {
            paymentRepository.verifyResults += PaymentStatus.PENDING
            val vm = buildViewModel(lockedCourse.id)
            advanceUntilIdle()

            vm.onIntent(CourseDetailIntent.SubscribeClicked)
            vm.onIntent(CourseDetailIntent.PayClicked)
            advanceUntilIdle()

            assertEquals(CheckoutStatus.FAILED, vm.state.value.checkout.status)
            assertEquals(PaymentFailure.PENDING, vm.state.value.checkout.failure)
            assertTrue(vm.state.value.isLocked)

            vm.onIntent(CourseDetailIntent.RetryPayment)
            advanceUntilIdle()

            assertEquals(CheckoutStatus.SUCCESS, vm.state.value.checkout.status)
            assertNull(vm.state.value.checkout.failure)
            assertFalse(vm.state.value.isLocked)
            assertEquals(1, paymentRepository.createdSessions.size, "No second checkout after PENDING")
            assertEquals(listOf("chk_0", "chk_0"), paymentRepository.verifiedSessionIds)
        }
}
