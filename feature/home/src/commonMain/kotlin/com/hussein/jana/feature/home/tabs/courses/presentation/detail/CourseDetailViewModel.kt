package com.hussein.jana.feature.home.tabs.courses.presentation.detail

import androidx.lifecycle.viewModelScope
import com.hussein.jana.core.common.mvi.BaseMviViewModel
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentStatus
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.CreateCourseCheckoutUseCase
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.GetCourseDetailUseCase
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.GetCourseUnitsUseCase
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.VerifyCoursePaymentUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Course details + checkout state machine.
 *
 * Business rule: the course is unlocked ONLY after the backend verifies the payment.
 * This ViewModel never sets `isSubscribed` itself; after a successful verification it
 * re-fetches the course and shows whatever the backend reports.
 */
class CourseDetailViewModel(
    private val courseId: String,
    openCheckout: Boolean,
    private val getCourseDetail: GetCourseDetailUseCase,
    private val getCourseUnits: GetCourseUnitsUseCase,
    private val createCourseCheckout: CreateCourseCheckoutUseCase,
    private val verifyCoursePayment: VerifyCoursePaymentUseCase,
) : BaseMviViewModel<CourseDetailUiState, CourseDetailIntent, CourseDetailEffect>(
    CourseDetailUiState(),
) {

    /** Open the sheet once after the first successful load when arriving from a list Subscribe tap. */
    private var pendingAutoOpen = openCheckout

    /**
     * Checkout session whose last verification came back PENDING. Retrying re-verifies this
     * session instead of opening a new checkout, so a payment already in flight (wallet, Fawry)
     * cannot be charged twice.
     */
    private var pendingSessionId: String? = null

    init {
        onIntent(CourseDetailIntent.Load)
    }

    override fun onIntent(intent: CourseDetailIntent) = when (intent) {
        CourseDetailIntent.Load -> loadDetail()
        is CourseDetailIntent.OpenLesson -> sendEffect(
            if (currentState().isLocked) {
                CourseDetailEffect.ShowLessonLocked
            } else {
                CourseDetailEffect.ShowLessonComingSoon
            },
        )
        CourseDetailIntent.Back -> sendEffect(CourseDetailEffect.NavigateBack)
        CourseDetailIntent.SubscribeClicked -> openCheckout()
        is CourseDetailIntent.PaymentMethodSelected -> selectMethod(intent)
        CourseDetailIntent.PayClicked -> startPaymentIf(CheckoutStatus.SELECTING_METHOD)
        CourseDetailIntent.RetryPayment -> startPaymentIf(CheckoutStatus.FAILED)
        CourseDetailIntent.DismissCheckout -> dismissCheckout()
    }

    private fun startPaymentIf(requiredStatus: CheckoutStatus) {
        if (currentState().checkout.status == requiredStatus) startPayment()
    }

    /** Ignored while a payment is processing or being verified. */
    private fun dismissCheckout() {
        if (!currentState().checkout.isBusy) setState { copy(checkout = CheckoutUiState()) }
    }

    private fun loadDetail() {
        setState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val detailDeferred = async { getCourseDetail(courseId) }
            val unitsDeferred = async { getCourseUnits(courseId) }

            val detailResult = detailDeferred.await()
            val unitsResult = unitsDeferred.await()

            if (detailResult.isSuccess) {
                val course = detailResult.getOrNull()
                val autoOpen = pendingAutoOpen && course?.isPurchasable == true
                pendingAutoOpen = false
                setState {
                    copy(
                        isLoading = false,
                        course = course,
                        units = unitsResult.getOrDefault(emptyList()),
                        error = null,
                        checkout = if (autoOpen) {
                            checkout.copy(status = CheckoutStatus.SELECTING_METHOD)
                        } else {
                            checkout
                        },
                    )
                }
            } else {
                setState {
                    copy(
                        isLoading = false,
                        error = detailResult.exceptionOrNull()?.message ?: "Failed to load course",
                    )
                }
            }
        }
    }

    private fun openCheckout() {
        val state = currentState()
        if (state.course?.isPurchasable != true || state.checkout.status != CheckoutStatus.IDLE) return
        setState { copy(checkout = CheckoutUiState(status = CheckoutStatus.SELECTING_METHOD)) }
    }

    private fun selectMethod(intent: CourseDetailIntent.PaymentMethodSelected) {
        val status = currentState().checkout.status
        if (status != CheckoutStatus.SELECTING_METHOD && status != CheckoutStatus.FAILED) return
        setState { copy(checkout = checkout.copy(selectedMethod = intent.method)) }
    }

    private fun startPayment() {
        val method = currentState().checkout.selectedMethod
        val reVerifySessionId = pendingSessionId
        if (reVerifySessionId != null) {
            setCheckoutStatus(CheckoutStatus.VERIFYING)
            viewModelScope.launch { verify(reVerifySessionId) }
            return
        }

        setCheckoutStatus(CheckoutStatus.PROCESSING)
        viewModelScope.launch {
            val session = createCourseCheckout(courseId, method).getOrElse {
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
        val verification = verifyCoursePayment(sessionId).getOrElse {
            fail(PaymentFailure.FAILED)
            return
        }
        if (verification.status != PaymentStatus.PENDING) pendingSessionId = null

        when {
            verification.status == PaymentStatus.PENDING -> fail(PaymentFailure.PENDING)
            verification.status == PaymentStatus.SUCCESS && verification.isSubscribed -> {
                // Reflect the backend's view of the course; never flip isSubscribed locally.
                val refreshed = getCourseDetail(courseId).getOrNull()
                if (refreshed?.isSubscribed == true) {
                    val units = getCourseUnits(courseId).getOrNull()
                    setState {
                        copy(
                            course = refreshed,
                            units = units ?: this.units,
                            checkout = CheckoutUiState(status = CheckoutStatus.SUCCESS),
                        )
                    }
                    sendEffect(CourseDetailEffect.SubscriptionActivated)
                } else {
                    fail(PaymentFailure.FAILED)
                }
            }
            else -> fail(PaymentFailure.FAILED)
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
