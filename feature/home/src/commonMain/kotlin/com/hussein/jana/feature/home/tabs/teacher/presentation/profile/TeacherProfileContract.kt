package com.hussein.jana.feature.home.tabs.teacher.presentation.profile

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState
import com.hussein.jana.feature.home.tabs.courses.domain.model.Course
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentMethod
import com.hussein.jana.feature.home.tabs.courses.presentation.detail.CheckoutUiState
import com.hussein.jana.feature.home.tabs.live.domain.model.LiveLesson
import com.hussein.jana.feature.home.tabs.teacher.domain.model.TeacherProfile
import com.hussein.jana.feature.home.tabs.teacher.domain.model.TeacherSubscription

/** Each section loads independently, so one failing section does not blank the whole tab. */
data class TeacherProfileUiState(
    val isProfileLoading: Boolean = true,
    val profile: TeacherProfile? = null,
    val profileError: String? = null,
    val isCoursesLoading: Boolean = true,
    val courses: List<Course> = emptyList(),
    val coursesError: String? = null,
    val isLessonsLoading: Boolean = true,
    val lessons: List<LiveLesson> = emptyList(),
    val lessonsError: String? = null,
    val isSubscriptionLoading: Boolean = true,
    val subscription: TeacherSubscription? = null,
    val subscriptionError: String? = null,
    val checkout: CheckoutUiState = CheckoutUiState(),
) : MviState

sealed interface TeacherProfileIntent : MviIntent {
    data object Load : TeacherProfileIntent
    data object RetryProfile : TeacherProfileIntent
    data object RetryCourses : TeacherProfileIntent
    data object RetryLessons : TeacherProfileIntent
    data object RetrySubscription : TeacherProfileIntent
    data object OpenChat : TeacherProfileIntent
    data class OpenCourse(val courseId: String) : TeacherProfileIntent
    data class SubscribeCourse(val courseId: String) : TeacherProfileIntent
    data class OpenLesson(val lessonId: String) : TeacherProfileIntent
    data class JoinZoom(val url: String) : TeacherProfileIntent
    data object SubscribeLiveLessons : TeacherProfileIntent
    data class PaymentMethodSelected(val method: PaymentMethod) : TeacherProfileIntent
    data object PayClicked : TeacherProfileIntent
    data object RetryPayment : TeacherProfileIntent
    data object DismissCheckout : TeacherProfileIntent
}

sealed interface TeacherProfileEffect : MviEffect {
    data object NavigateToChat : TeacherProfileEffect
    data class NavigateToCourseDetail(
        val courseId: String,
        val openCheckout: Boolean = false,
    ) : TeacherProfileEffect
    data class NavigateToLessonDetail(val lessonId: String) : TeacherProfileEffect
    data class OpenZoomUrl(val url: String) : TeacherProfileEffect
    data object ShowNoZoomLink : TeacherProfileEffect
    data object LiveSubscriptionActivated : TeacherProfileEffect
}
