package com.hussein.jana.feature.home.tabs.courses.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.JanaBottomSheet
import com.hussein.jana.core.designsystem.JanaLockedBadge
import com.hussein.jana.core.designsystem.JanaStatusChip
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.btn_retry
import com.hussein.jana.core.designsystem.course_detail_title
import com.hussein.jana.core.designsystem.course_lesson_coming_soon
import com.hussein.jana.core.designsystem.course_locked
import com.hussein.jana.core.designsystem.course_progress_label
import com.hussein.jana.core.designsystem.course_subscribe_to_unlock
import com.hussein.jana.core.designsystem.error_generic
import com.hussein.jana.core.designsystem.payment_subscription_activated
import com.hussein.jana.feature.home.tabs.courses.presentation.common.formatted
import com.hussein.jana.feature.home.tabs.courses.presentation.detail.components.CourseCheckoutSheetContent
import com.hussein.jana.feature.home.tabs.courses.presentation.detail.components.CourseSubscribeBar
import com.hussein.jana.feature.home.tabs.courses.presentation.detail.components.CourseUnitSection
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CourseDetailScreen(
    courseId: String,
    onBack: () -> Unit,
    openCheckout: Boolean = false,
    viewModel: CourseDetailViewModel = koinViewModel { parametersOf(courseId, openCheckout) },
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val lessonComingSoon = stringResource(Res.string.course_lesson_coming_soon)
    val subscribeToUnlock = stringResource(Res.string.course_subscribe_to_unlock)
    val subscriptionActivated = stringResource(Res.string.payment_subscription_activated)

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                CourseDetailEffect.ShowLessonComingSoon ->
                    snackbarHostState.showSnackbar(lessonComingSoon)
                CourseDetailEffect.ShowLessonLocked ->
                    snackbarHostState.showSnackbar(subscribeToUnlock)
                CourseDetailEffect.SubscriptionActivated ->
                    snackbarHostState.showSnackbar(subscriptionActivated)
                CourseDetailEffect.NavigateBack -> onBack()
            }
        }
    }

    CourseDetailContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CourseDetailContent(
    state: CourseDetailUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onIntent: (CourseDetailIntent) -> Unit,
) {
    val priceText = state.course?.price?.formatted()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        // Course name once loaded; generic title only while loading.
                        text = state.course?.title ?: stringResource(Res.string.course_detail_title),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        bottomBar = {
            if (state.isLocked) {
                CourseSubscribeBar(
                    priceText = priceText,
                    onSubscribeClick = { onIntent(CourseDetailIntent.SubscribeClicked) },
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = ScholarlyTeal)
                }
            }
            // Finding 3: error branch prevents blank body when course is null but error is set
            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(Res.string.error_generic),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = { onIntent(CourseDetailIntent.Load) },
                        ) {
                            Text(text = stringResource(Res.string.btn_retry))
                        }
                    }
                }
            }
            state.course != null -> {
                val course = state.course
                val progress = if (course.totalLessons > 0) {
                    course.completedLessons.toFloat() / course.totalLessons.toFloat()
                } else {
                    0f
                }
                val progressLabel = stringResource(Res.string.course_progress_label)
                    .replace("%1\$d", course.completedLessons.toString())
                    .replace("%2\$d", course.totalLessons.toString())

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // 1) Hero header
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = course.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = course.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                course.subjects.forEach { subject ->
                                    JanaStatusChip(
                                        status = subject,
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (state.isLocked) {
                                JanaLockedBadge(text = stringResource(Res.string.course_locked))
                            } else {
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = ScholarlyTeal,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = progressLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    // 2) Units (expandable); locked courses show them as a syllabus preview
                    items(state.units, key = { it.id }) { unit ->
                        CourseUnitSection(
                            unit = unit,
                            onLessonClick = { lesson ->
                                onIntent(CourseDetailIntent.OpenLesson(lesson))
                            },
                            isLocked = state.isLocked,
                        )
                    }
                }
            }
        }
    }

    val course = state.course
    if (course != null && state.checkout.isVisible) {
        JanaBottomSheet(
            onDismissRequest = { onIntent(CourseDetailIntent.DismissCheckout) },
            dismissEnabled = !state.checkout.isBusy,
        ) {
            CourseCheckoutSheetContent(
                courseTitle = course.title,
                priceText = priceText,
                checkout = state.checkout,
                onMethodSelected = { onIntent(CourseDetailIntent.PaymentMethodSelected(it)) },
                onPay = { onIntent(CourseDetailIntent.PayClicked) },
                onRetry = { onIntent(CourseDetailIntent.RetryPayment) },
                onCancel = { onIntent(CourseDetailIntent.DismissCheckout) },
            )
        }
    }
}
