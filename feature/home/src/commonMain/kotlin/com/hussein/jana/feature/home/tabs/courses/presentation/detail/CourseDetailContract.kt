package com.hussein.jana.feature.home.tabs.courses.presentation.detail

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState
import com.hussein.jana.feature.home.tabs.courses.domain.model.Course
import com.hussein.jana.feature.home.tabs.courses.domain.model.CourseLesson
import com.hussein.jana.feature.home.tabs.courses.domain.model.CourseUnit
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentMethod

enum class CheckoutStatus { IDLE, SELECTING_METHOD, PROCESSING, VERIFYING, SUCCESS, FAILED }

enum class PaymentFailure { FAILED, PENDING }

data class CheckoutUiState(
    val status: CheckoutStatus = CheckoutStatus.IDLE,
    val selectedMethod: PaymentMethod = PaymentMethod.CARD,
    val availableMethods: List<PaymentMethod> = PaymentMethod.entries,
    val failure: PaymentFailure? = null,
) {
    val isVisible: Boolean
        get() = status == CheckoutStatus.SELECTING_METHOD ||
            status == CheckoutStatus.PROCESSING ||
            status == CheckoutStatus.VERIFYING ||
            status == CheckoutStatus.FAILED

    /** While busy the sheet cannot be dismissed and inputs are disabled. */
    val isBusy: Boolean
        get() = status == CheckoutStatus.PROCESSING || status == CheckoutStatus.VERIFYING
}

data class CourseDetailUiState(
    val isLoading: Boolean = true,
    val course: Course? = null,
    val units: List<CourseUnit> = emptyList(),
    val error: String? = null,
    val checkout: CheckoutUiState = CheckoutUiState(),
) : MviState {
    val isLocked: Boolean get() = course?.isSubscribed == false
}

sealed interface CourseDetailIntent : MviIntent {
    data object Load : CourseDetailIntent
    data class OpenLesson(val lesson: CourseLesson) : CourseDetailIntent
    data object Back : CourseDetailIntent
    data object SubscribeClicked : CourseDetailIntent
    data class PaymentMethodSelected(val method: PaymentMethod) : CourseDetailIntent
    data object PayClicked : CourseDetailIntent
    data object RetryPayment : CourseDetailIntent
    data object DismissCheckout : CourseDetailIntent
}

sealed interface CourseDetailEffect : MviEffect {
    data object ShowLessonComingSoon : CourseDetailEffect
    data object ShowLessonLocked : CourseDetailEffect
    data object SubscriptionActivated : CourseDetailEffect
    data object NavigateBack : CourseDetailEffect
}
