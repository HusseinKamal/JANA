package com.hussein.jana.feature.home.tabs.teacher.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.JanaBottomSheet
import com.hussein.jana.core.designsystem.JanaCourseCard
import com.hussein.jana.core.designsystem.JanaEmptyState
import com.hussein.jana.core.designsystem.JanaErrorState
import com.hussein.jana.core.designsystem.JanaLoadingState
import com.hussein.jana.core.designsystem.JanaNavigationCard
import com.hussein.jana.core.designsystem.JanaProfileHeader
import com.hussein.jana.core.designsystem.JanaSectionHeader
import com.hussein.jana.core.designsystem.JanaSubscriptionStatus
import com.hussein.jana.core.designsystem.JanaSubscriptionStatusCard
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.btn_retry
import com.hussein.jana.core.designsystem.courses_no_courses
import com.hussein.jana.core.designsystem.error_generic
import com.hussein.jana.core.designsystem.live_no_upcoming
import com.hussein.jana.core.designsystem.live_no_zoom_link
import com.hussein.jana.core.designsystem.openZoomUrl
import com.hussein.jana.core.designsystem.payment_subscription_activated
import com.hussein.jana.core.designsystem.subscription_active_until
import com.hussein.jana.core.designsystem.subscription_expired_on
import com.hussein.jana.core.designsystem.subscription_live_plan_title
import com.hussein.jana.core.designsystem.subscription_live_unlock_desc
import com.hussein.jana.core.designsystem.subscription_renew_btn
import com.hussein.jana.core.designsystem.subscription_status_active
import com.hussein.jana.core.designsystem.subscription_status_expired
import com.hussein.jana.core.designsystem.subscription_status_not_subscribed
import com.hussein.jana.core.designsystem.subscription_subscribe_btn
import com.hussein.jana.core.designsystem.teacher_profile_chat_subtitle
import com.hussein.jana.core.designsystem.teacher_profile_chat_title
import com.hussein.jana.core.designsystem.teacher_profile_courses_section
import com.hussein.jana.core.designsystem.teacher_profile_live_section
import com.hussein.jana.core.designsystem.teacher_profile_photo_cd
import com.hussein.jana.core.navigation.AppNavigator
import com.hussein.jana.core.navigation.HomeRoute
import com.hussein.jana.feature.home.common.teacherDisplayName
import com.hussein.jana.feature.home.tabs.courses.presentation.common.formatted
import com.hussein.jana.feature.home.tabs.courses.presentation.common.toCardUiModel
import com.hussein.jana.feature.home.tabs.courses.presentation.detail.CheckoutUiState
import com.hussein.jana.feature.home.tabs.courses.presentation.detail.components.CourseCheckoutSheetContent
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonStatus
import com.hussein.jana.feature.home.tabs.live.presentation.list.components.NextLessonItem
import com.hussein.jana.feature.home.tabs.live.presentation.list.components.UpcomingLessonCard
import com.hussein.jana.feature.home.tabs.teacher.domain.model.SubscriptionStatus
import com.hussein.jana.feature.home.tabs.teacher.domain.model.TeacherSubscription
import com.hussein.jana.feature.home.tabs.teacher.presentation.common.formatShortDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

// ── Entry (stateful) ──────────────────────────────────────────────────────────

@Composable
fun TeacherProfileScreen(
    viewModel: TeacherProfileViewModel = koinViewModel(),
    navigator: AppNavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val noZoomLink = stringResource(Res.string.live_no_zoom_link)
    val subscriptionActivated = stringResource(Res.string.payment_subscription_activated)

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                TeacherProfileEffect.NavigateToChat -> navigator.navigate(HomeRoute.TeacherChat)
                is TeacherProfileEffect.NavigateToCourseDetail ->
                    navigator.navigate(HomeRoute.CourseDetail(effect.courseId, effect.openCheckout))
                is TeacherProfileEffect.NavigateToLessonDetail ->
                    navigator.navigate(HomeRoute.LessonDetail(effect.lessonId))
                is TeacherProfileEffect.OpenZoomUrl -> openZoomUrl(effect.url)
                TeacherProfileEffect.ShowNoZoomLink -> snackbarHostState.showSnackbar(noZoomLink)
                TeacherProfileEffect.LiveSubscriptionActivated ->
                    snackbarHostState.showSnackbar(subscriptionActivated)
            }
        }
    }

    TeacherProfileContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
    )
}

// ── Stateless content ─────────────────────────────────────────────────────────

@Composable
fun TeacherProfileContent(
    state: TeacherProfileUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (TeacherProfileIntent) -> Unit,
) {
    val errorText = stringResource(Res.string.error_generic)
    val retryLabel = stringResource(Res.string.btn_retry)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        // Nested inside HomeScreen's Scaffold, which already handles system insets.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // 1) Teacher header
            item(key = "profile") {
                val profile = state.profile
                when {
                    state.isProfileLoading -> JanaLoadingState()
                    state.profileError != null || profile == null -> JanaErrorState(
                        message = errorText,
                        retryLabel = retryLabel,
                        onRetry = { onIntent(TeacherProfileIntent.RetryProfile) },
                    )
                    else -> JanaProfileHeader(
                        name = teacherDisplayName(profile.name, profile.gender),
                        subtitle = profile.subject,
                        bio = profile.bio,
                        imageUrl = profile.photoUrl,
                        avatarContentDescription = stringResource(Res.string.teacher_profile_photo_cd),
                    )
                }
            }

            // 2) Chat with teacher
            item(key = "chat") {
                JanaNavigationCard(
                    title = stringResource(Res.string.teacher_profile_chat_title),
                    subtitle = stringResource(Res.string.teacher_profile_chat_subtitle),
                    icon = Icons.AutoMirrored.Rounded.Chat,
                    onClick = { onIntent(TeacherProfileIntent.OpenChat) },
                )
            }

            // 3) Courses & subscriptions
            item(key = "courses_header") {
                JanaSectionHeader(title = stringResource(Res.string.teacher_profile_courses_section))
            }
            when {
                state.isCoursesLoading -> item(key = "courses_loading") { JanaLoadingState() }
                state.coursesError != null -> item(key = "courses_error") {
                    JanaErrorState(
                        message = errorText,
                        retryLabel = retryLabel,
                        onRetry = { onIntent(TeacherProfileIntent.RetryCourses) },
                    )
                }
                state.courses.isEmpty() -> item(key = "courses_empty") {
                    JanaEmptyState(message = stringResource(Res.string.courses_no_courses))
                }
                else -> items(state.courses, key = { "course_${it.id}" }) { course ->
                    JanaCourseCard(
                        model = course.toCardUiModel(),
                        onClick = { onIntent(TeacherProfileIntent.OpenCourse(course.id)) },
                        onSubscribeClick = { onIntent(TeacherProfileIntent.SubscribeCourse(course.id)) },
                    )
                }
            }

            // 4) Live lessons & subscription
            item(key = "live_header") {
                JanaSectionHeader(title = stringResource(Res.string.teacher_profile_live_section))
            }
            item(key = "live_subscription") {
                val subscription = state.subscription
                when {
                    state.isSubscriptionLoading -> JanaLoadingState()
                    state.subscriptionError != null || subscription == null -> JanaErrorState(
                        message = errorText,
                        retryLabel = retryLabel,
                        onRetry = { onIntent(TeacherProfileIntent.RetrySubscription) },
                    )
                    else -> LiveSubscriptionCard(
                        subscription = subscription,
                        checkout = state.checkout,
                        onSubscribe = { onIntent(TeacherProfileIntent.SubscribeLiveLessons) },
                    )
                }
            }
            when {
                state.isLessonsLoading -> item(key = "lessons_loading") { JanaLoadingState() }
                state.lessonsError != null -> item(key = "lessons_error") {
                    JanaErrorState(
                        message = errorText,
                        retryLabel = retryLabel,
                        onRetry = { onIntent(TeacherProfileIntent.RetryLessons) },
                    )
                }
                state.lessons.isEmpty() -> item(key = "lessons_empty") {
                    JanaEmptyState(message = stringResource(Res.string.live_no_upcoming))
                }
                else -> items(state.lessons, key = { "lesson_${it.id}" }) { lesson ->
                    if (lesson.status == LessonStatus.LIVE_NOW) {
                        UpcomingLessonCard(
                            lesson = lesson,
                            onLessonClick = { onIntent(TeacherProfileIntent.OpenLesson(lesson.id)) },
                            onJoinZoomClick = { onIntent(TeacherProfileIntent.JoinZoom(lesson.zoomUrl)) },
                        )
                    } else {
                        NextLessonItem(
                            lesson = lesson,
                            onClick = { onIntent(TeacherProfileIntent.OpenLesson(lesson.id)) },
                        )
                    }
                }
            }
        }
    }

    val subscription = state.subscription
    if (subscription != null && state.checkout.isVisible) {
        JanaBottomSheet(
            onDismissRequest = { onIntent(TeacherProfileIntent.DismissCheckout) },
            dismissEnabled = !state.checkout.isBusy,
        ) {
            CourseCheckoutSheetContent(
                itemTitle = subscription.planName,
                priceText = subscription.price?.formatted(),
                checkout = state.checkout,
                onMethodSelected = { onIntent(TeacherProfileIntent.PaymentMethodSelected(it)) },
                onPay = { onIntent(TeacherProfileIntent.PayClicked) },
                onRetry = { onIntent(TeacherProfileIntent.RetryPayment) },
                onCancel = { onIntent(TeacherProfileIntent.DismissCheckout) },
            )
        }
    }
}

// ── Live subscription card mapping ────────────────────────────────────────────

@Composable
private fun LiveSubscriptionCard(
    subscription: TeacherSubscription,
    checkout: CheckoutUiState,
    onSubscribe: () -> Unit,
) {
    val expiry = subscription.expiresAtMs?.let(::formatShortDate)
    val (status, statusLabel) = when (subscription.status) {
        SubscriptionStatus.ACTIVE ->
            JanaSubscriptionStatus.ACTIVE to stringResource(Res.string.subscription_status_active)
        SubscriptionStatus.EXPIRED ->
            JanaSubscriptionStatus.EXPIRED to stringResource(Res.string.subscription_status_expired)
        SubscriptionStatus.NOT_SUBSCRIBED ->
            JanaSubscriptionStatus.NOT_SUBSCRIBED to stringResource(Res.string.subscription_status_not_subscribed)
    }
    val detailText = when (subscription.status) {
        SubscriptionStatus.ACTIVE -> expiry?.let { stringResource(Res.string.subscription_active_until, it) }
        SubscriptionStatus.EXPIRED -> expiry?.let { stringResource(Res.string.subscription_expired_on, it) }
        SubscriptionStatus.NOT_SUBSCRIBED -> stringResource(Res.string.subscription_live_unlock_desc)
    }
    val actionLabel = when (subscription.status) {
        SubscriptionStatus.NOT_SUBSCRIBED -> stringResource(Res.string.subscription_subscribe_btn)
        SubscriptionStatus.EXPIRED -> stringResource(Res.string.subscription_renew_btn)
        SubscriptionStatus.ACTIVE -> null
    }

    JanaSubscriptionStatusCard(
        title = stringResource(Res.string.subscription_live_plan_title),
        status = status,
        statusLabel = statusLabel,
        detailText = detailText,
        priceText = if (subscription.status != SubscriptionStatus.ACTIVE) subscription.price?.formatted() else null,
        actionLabel = actionLabel,
        onAction = if (subscription.isPurchasable) onSubscribe else null,
        isActionLoading = checkout.isBusy,
    )
}
