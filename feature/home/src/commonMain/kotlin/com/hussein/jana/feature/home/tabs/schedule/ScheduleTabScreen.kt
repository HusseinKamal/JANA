package com.hussein.jana.feature.home.tabs.schedule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.OutlineVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.VellumCanvas
import com.hussein.jana.core.designsystem.openNativeCalendar
import com.hussein.jana.core.designsystem.schedule_sync
import com.hussein.jana.core.designsystem.schedule_action_ai_socratic
import com.hussein.jana.core.designsystem.schedule_action_book
import com.hussein.jana.core.designsystem.schedule_action_coming_soon
import com.hussein.jana.core.designsystem.schedule_action_joining
import com.hussein.jana.core.designsystem.schedule_action_prep_ai
import com.hussein.jana.core.designsystem.schedule_action_reserve
import com.hussein.jana.core.designsystem.schedule_action_review_notes
import com.hussein.jana.core.designsystem.schedule_action_syllabus
import com.hussein.jana.core.designsystem.schedule_apple_coming_soon
import com.hussein.jana.core.designsystem.schedule_google_coming_soon
import com.hussein.jana.core.designsystem.schedule_sync_coming_soon
import com.hussein.jana.core.designsystem.schedule_topup_coming_soon
import com.hussein.jana.feature.home.tabs.schedule.components.DaySectionHeader
import com.hussein.jana.feature.home.tabs.schedule.components.ScheduleCreditsBanner
import com.hussein.jana.feature.home.tabs.schedule.components.ScheduleWeekSection
import com.hussein.jana.feature.home.tabs.schedule.components.SessionCard
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// ── Entry point (stateful) ────────────────────────────────────────────────────

/**
 * Stateful entry-point for the Schedule tab.
 *
 * Owns the snackbar host state and collects one-shot [ScheduleTabEffect]s.
 * Localised strings are injected into the ViewModel via [LaunchedEffect] using
 * the same pattern as [com.hussein.jana.feature.home.tabs.teachers.TeachersTabScreen].
 */
@Composable
fun ScheduleTabScreen() {
    val viewModel: ScheduleTabViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Resolve localised strings in composable scope and push into the ViewModel
    // so snackbar messages are always in the current device language.
    val syncComingSoon = stringResource(Res.string.schedule_sync_coming_soon)
    val topUpComingSoon = stringResource(Res.string.schedule_topup_coming_soon)
    val googleComingSoon = stringResource(Res.string.schedule_google_coming_soon)
    val appleComingSoon = stringResource(Res.string.schedule_apple_coming_soon)
    val joiningSession = stringResource(Res.string.schedule_action_joining)
    val prepWithAi = stringResource(Res.string.schedule_action_prep_ai)
    val reviewNotes = stringResource(Res.string.schedule_action_review_notes)
    val aiSocratic = stringResource(Res.string.schedule_action_ai_socratic)
    val reserveSession = stringResource(Res.string.schedule_action_reserve)
    val bookSeat = stringResource(Res.string.schedule_action_book)
    val viewSyllabus = stringResource(Res.string.schedule_action_syllabus)
    val actionComingSoon = stringResource(Res.string.schedule_action_coming_soon)

    LaunchedEffect(
        syncComingSoon, topUpComingSoon, googleComingSoon, appleComingSoon,
        joiningSession, prepWithAi, reviewNotes, aiSocratic,
        reserveSession, bookSeat, viewSyllabus, actionComingSoon,
    ) {
        viewModel.updateStrings(
            ScheduleStrings(
                syncComingSoon = syncComingSoon,
                topUpComingSoon = topUpComingSoon,
                googleComingSoon = googleComingSoon,
                appleComingSoon = appleComingSoon,
                joiningSession = joiningSession,
                prepWithAi = prepWithAi,
                reviewNotes = reviewNotes,
                aiSocratic = aiSocratic,
                reserveSession = reserveSession,
                bookSeat = bookSeat,
                viewSyllabus = viewSyllabus,
                actionComingSoon = actionComingSoon,
            ),
        )
    }

    // Collect one-shot effects
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ScheduleTabEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                ScheduleTabEffect.OpenCalendar    -> openNativeCalendar()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ScheduleTabContent(
            state = state,
            onIntent = viewModel::onIntent,
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
        )
    }
}

// ── Stateless content ─────────────────────────────────────────────────────────

@Composable
internal fun ScheduleTabContent(
    state: ScheduleTabState,
    onIntent: (ScheduleTabIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VellumCanvas),
    ) {
        // ── Scrollable body — header removed (title is in top app bar) ──────
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                ScheduleCreditsBanner(
                    creditsAvailable = state.creditsAvailable,
                    creditsReserved = state.creditsReserved,
                    onTopUp = { onIntent(ScheduleTabIntent.TopUpClicked) },
                )
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }

            item {
                ScheduleWeekSection(
                    state = state,
                    onDaySelected = { date -> onIntent(ScheduleTabIntent.DaySelected(date)) },
                    onFilterSelected = { key -> onIntent(ScheduleTabIntent.FilterSelected(key)) },
                )
            }

            // "Sync with Calendar" chip — opens native calendar on device
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End,
                ) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = { onIntent(ScheduleTabIntent.SyncCalendarClicked) },
                        border = BorderStroke(1.dp, OutlineVariant),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CalendarMonth,
                            contentDescription = null,
                            tint = ScholarlyTeal,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(Res.string.schedule_sync),
                            style = MaterialTheme.typography.labelSmall,
                            color = ScholarlyTeal,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Group sessions by dayGroupKey in canonical display order
            val groups = state.filteredSessions.groupBy { it.dayGroupKey }
            val order = listOf("today", "tomorrow", "thursday", "saturday", "sunday")

            order.forEach { key ->
                val sessions = groups[key]
                if (!sessions.isNullOrEmpty()) {
                    item {
                        DaySectionHeader(
                            session = sessions.first(),
                            state = state,
                        )
                    }
                    items(sessions) { session ->
                        SessionCard(
                            session = session,
                            onActionClick = { actionId ->
                                onIntent(
                                    ScheduleTabIntent.SessionActionClicked(session.id, actionId),
                                )
                            },
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }

            // Clear bottom nav bar (CalendarIntegrationCard removed)
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
