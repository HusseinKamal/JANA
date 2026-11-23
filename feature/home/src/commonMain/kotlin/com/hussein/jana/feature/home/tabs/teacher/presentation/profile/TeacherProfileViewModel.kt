package com.hussein.jana.feature.home.tabs.teacher.presentation.profile

import androidx.lifecycle.viewModelScope
import com.hussein.jana.core.common.mvi.BaseMviViewModel
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentStatus
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.GetCoursesUseCase
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.ObserveCoursesUseCase
import com.hussein.jana.feature.home.tabs.courses.presentation.detail.CheckoutStatus
import com.hussein.jana.feature.home.tabs.courses.presentation.detail.CheckoutUiState
import com.hussein.jana.feature.home.tabs.courses.presentation.detail.PaymentFailure
import com.hussein.jana.feature.home.tabs.teacher.domain.model.SubscriptionStatus
import com.hussein.jana.feature.home.tabs.teacher.domain.usecase.CreateLiveSubscriptionCheckoutUseCase
import com.hussein.jana.feature.home.tabs.teacher.domain.usecase.GetActiveTeacherProfileUseCase
import com.hussein.jana.feature.home.tabs.teacher.domain.usecase.GetLiveLessonsSubscriptionUseCase
import com.hussein.jana.feature.home.tabs.teacher.domain.usecase.GetTeacherUpcomingLessonsUseCase
import com.hussein.jana.feature.home.tabs.teacher.domain.usecase.VerifyLiveSubscriptionPaymentUseCase
import kotlinx.coroutines.launch

/**
 * Teacher Profile tab for the active teacher: profile, courses, live lessons and the
 * live-lessons subscription checkout.
 *
 * Business rule: the live subscription becomes ACTIVE only after the backend verifies the
 * payment. This ViewModel never sets ACTIVE itself; after a successful verification it
 * re-fetches the subscription and shows whatever the backend reports.
 *
 * TODO: gate lesson access (Join / details) by the live subscription once the backend
 *  enforces it; today lessons stay viewable and joinable regardless of the subscription.
 */
class TeacherProfileViewModel(
    private val getActiveTeacherProfile: GetActiveTeacherProfileUseCase,
    private val getCourses: GetCoursesUseCase,
    private val observeCourses: ObserveCoursesUseCase,
    private val getTeacherUpcomingLessons: GetTeacherUpcomingLessonsUseCase,
    private val getLiveLessonsSubscription: GetLiveLessonsSubscriptionUseCase,
    private val createLiveSubscriptionCheckout: CreateLiveSubscriptionCheckoutUseCase,
    private val verifyLiveSubscriptionPayment: VerifyLiveSubscriptionPaymentUseCase,
) : BaseMviViewModel<TeacherProfileUiState, TeacherProfileIntent, TeacherProfileEffect>(
    TeacherProfileUiState(),
) {

    /**
     * Checkout session whose last verification came back PENDING. Retrying re-verifies this
     * session instead of opening a new checkout, so a payment already in flight cannot be
     * charged twice.
     */
    private var pendingSessionId: String? = null

    init {
        onIntent(TeacherProfileIntent.Load)
        // A course subscription verified in Course Details updates the shared catalogue;
        // apply it only after the first load so the initial spinner is not skipped.
        viewModelScope.launch {
            observeCourses().collect { list ->
                if (!currentState().isCoursesLoading) setState { copy(courses = list) }
            }
        }
    }

    override fun onIntent(intent: TeacherProfileIntent) = when (intent) {
        TeacherProfileIntent.Load -> {
            loadProfile()
            loadCourses()
            loadLessons()
            loadSubscription()
        }
        TeacherProfileIntent.RetryProfile -> loadProfile()
        TeacherProfileIntent.RetryCourses -> loadCourses()
        TeacherProfileIntent.RetryLessons -> loadLessons()
        TeacherProfileIntent.RetrySubscription -> loadSubscription()
        TeacherProfileIntent.OpenChat -> sendEffect(TeacherProfileEffect.NavigateToChat)
        is TeacherProfileIntent.OpenCourse ->
            sendEffect(TeacherProfileEffect.NavigateToCourseDetail(intent.courseId))
        // Course checkout lives in Course Details; navigate there with the sheet open.
        is TeacherProfileIntent.SubscribeCourse ->
            sendEffect(TeacherProfileEffect.NavigateToCourseDetail(intent.courseId, openCheckout = true))
        is TeacherProfileIntent.OpenLesson ->
            sendEffect(TeacherProfileEffect.NavigateToLessonDetail(intent.lessonId))
        is TeacherProfileIntent.JoinZoom -> sendEffect(
            if (intent.url.isBlank()) {
                TeacherProfileEffect.ShowNoZoomLink
            } else {
                TeacherProfileEffect.OpenZoomUrl(intent.url)
            },
        )
        TeacherProfileIntent.SubscribeLiveLessons -> openCheckout()
        is TeacherProfileIntent.PaymentMethodSelected -> selectMethod(intent)
        TeacherProfileIntent.PayClicked -> startPaymentIf(CheckoutStatus.SELECTING_METHOD)
        TeacherProfileIntent.RetryPayment -> startPaymentIf(CheckoutStatus.FAILED)
        TeacherProfileIntent.DismissCheckout -> dismissCheckout()
    }

    // ── Section loaders ──────────────────────────────────────────────────────

    private fun loadProfile() {
        setState { copy(isProfileLoading = true, profileError = null) }
        viewModelScope.launch {
            getActiveTeacherProfile()
                .onSuccess { setState { copy(isProfileLoading = false, profile = it) } }
                .onFailure {
                    setState {
                        copy(isProfileLoading = false, profileError = it.message ?: "Failed to load teacher")
                    }
                }
        }
    }

    private fun loadCourses() {
        setState { copy(isCoursesLoading = true, coursesError = null) }
        viewModelScope.launch {
            getCourses()
                .onSuccess { setState { copy(isCoursesLoading = false, courses = it) } }
                .onFailure {
                    setState {
                        copy(isCoursesLoading = false, coursesError = it.message ?: "Failed to load courses")
                    }
                }
        }
    }

    private fun loadLessons() {
        setState { copy(isLessonsLoading = true, lessonsError = null) }
        viewModelScope.launch {
            getTeacherUpcomingLessons()
                .onSuccess { setState { copy(isLessonsLoading = false, lessons = it) } }
                .onFailure {
                    setState {
                        copy(isLessonsLoading = false, lessonsError = it.message ?: "Failed to load lessons")
                    }
                }
        }
    }

    private fun loadSubscription() {
        setState { copy(isSubscriptionLoading = true, subscriptionError = null) }
        viewModelScope.launch {
            getLiveLessonsSubscription()
                .onSuccess { setState { copy(isSubscriptionLoading = false, subscription = it) } }
                .onFailure {
                    setState {
                        copy(
                            isSubscriptionLoading = false,
                            subscriptionError = it.message ?: "Failed to load subscription",
                        )
                    }
                }
        }
    }

    // ── Live subscription checkout ───────────────────────────────────────────

    private fun openCheckout() {
        val state = currentState()
        if (state.subscription?.isPurchasable != true || state.checkout.status != CheckoutStatus.IDLE) return
        setState { copy(checkout = CheckoutUiState(status = CheckoutStatus.SELECTING_METHOD)) }
    }

    private fun selectMethod(intent: TeacherProfileIntent.PaymentMethodSelected) {
        val status = currentState().checkout.status
        if (status != CheckoutStatus.SELECTING_METHOD && status != CheckoutStatus.FAILED) return
        setState { copy(checkout = checkout.copy(selectedMethod = intent.method)) }
    }

    private fun startPaymentIf(requiredStatus: CheckoutStatus) {
        if (currentState().checkout.status == requiredStatus) startPayment()
    }

    /** Ignored while a payment is processing or being verified. */
    private fun dismissCheckout() {
        if (!currentState().checkout.isBusy) setState { copy(checkout = CheckoutUiState()) }
    }

    private fun startPayment() {
        val reVerifySessionId = pendingSessionId
        if (reVerifySessionId != null) {
            setCheckoutStatus(CheckoutStatus.VERIFYING)
            viewModelScope.launch { verify(reVerifySessionId) }
            return
        }

        val planId = currentState().subscription?.planId ?: return
        val method = currentState().checkout.selectedMethod
        setCheckoutStatus(CheckoutStatus.PROCESSING)
        viewModelScope.launch {
            val session = createLiveSubscriptionCheckout(planId, method).getOrElse {
                fail(PaymentFailure.FAILED)
                return@launch
            }

            // TODO: when session.checkoutUrl != null, emit an effect to launch it via openUrl()
            //  and verify on return.

            setCheckoutStatus(CheckoutStatus.VERIFYING)
            verify(session.id)
        }
    }

    private suspend fun verify(sessionId: String) {
        // Keep the session until the backend gives a final answer; a network error or PENDING
        // means the money may already be on its way, so the next attempt re-verifies it.
        pendingSessionId = sessionId
        val verification = verifyLiveSubscriptionPayment(sessionId).getOrElse {
            fail(PaymentFailure.FAILED)
            return
        }
        when {
            verification.status == PaymentStatus.PENDING -> fail(PaymentFailure.PENDING)
            verification.status == PaymentStatus.SUCCESS && verification.isSubscribed -> {
                // Reflect the backend's view of the subscription; never set ACTIVE locally.
                // The session is kept until the re-fetch confirms ACTIVE, so a failed re-fetch
                // makes Retry re-verify the paid session instead of opening a second checkout.
                val refreshed = getLiveLessonsSubscription().getOrNull()
                if (refreshed?.status == SubscriptionStatus.ACTIVE) {
                    pendingSessionId = null
                    setState {
                        copy(
                            subscription = refreshed,
                            checkout = CheckoutUiState(status = CheckoutStatus.SUCCESS),
                        )
                    }
                    sendEffect(TeacherProfileEffect.LiveSubscriptionActivated)
                } else {
                    fail(PaymentFailure.FAILED)
                }
            }
            else -> {
                pendingSessionId = null
                fail(PaymentFailure.FAILED)
            }
        }
    }

    private fun setCheckoutStatus(status: CheckoutStatus) {
        setState { copy(checkout = checkout.copy(status = status, failure = null)) }
    }

    private fun fail(failure: PaymentFailure) {
        setState {
            copy(checkout = checkout.copy(status = CheckoutStatus.FAILED, failure = failure))
        }
    }
}
