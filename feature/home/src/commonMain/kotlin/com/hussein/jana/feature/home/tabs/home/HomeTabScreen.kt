package com.hussein.jana.feature.home.tabs.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hussein.jana.feature.home.tabs.home.components.ActiveSessionBanner
import com.hussein.jana.feature.home.tabs.home.components.AiCompanionCard
import com.hussein.jana.feature.home.tabs.home.components.ConceptMasterySection
import com.hussein.jana.feature.home.tabs.home.components.CreditsBanner
import com.hussein.jana.feature.home.tabs.home.components.GreetingHeader
import com.hussein.jana.feature.home.tabs.home.components.LiveSessionCard
import com.hussein.jana.feature.home.tabs.home.components.UpcomingSessionsSection
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// ── Entry point (stateful) ────────────────────────────────────────────────────

/**
 * @param credits Number of credits owned by the student — provided by the
 *   parent [HomeScreen] from [HomeState] so there is a single source of truth.
 */
@Composable
fun HomeTabScreen(credits: Int) {
    val viewModel: HomeTabViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Step 1 — Collect the effect and store the StringResource in composable state.
    //           (Cannot call stringResource() inside a suspend lambda.)
    var pendingSnackbarRes by remember { mutableStateOf<StringResource?>(null) }
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeTabEffect.ShowSnackbar -> pendingSnackbarRes = effect.messageRes
            }
        }
    }

    // Step 2 — Resolve the StringResource in composable scope, then show the snackbar.
    val resolvedSnackbar = pendingSnackbarRes?.let { stringResource(it) }
    LaunchedEffect(resolvedSnackbar) {
        if (resolvedSnackbar != null) {
            snackbarHostState.showSnackbar(resolvedSnackbar)
            pendingSnackbarRes = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomeTabContent(
            state = state,
            credits = credits,
            onIntent = viewModel::onIntent,
        )

        // Sticky credits banner pinned to the bottom
        CreditsBanner(
            credits = credits,
            onRefillClick = { viewModel.onIntent(HomeTabIntent.RefillPlanClicked) },
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        // Snackbar host sits above the banner
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp),
        )
    }
}

// ── Stateless content ─────────────────────────────────────────────────────────

@Composable
private fun HomeTabContent(
    state: HomeTabState,
    credits: Int,
    onIntent: (HomeTabIntent) -> Unit,
) {
    // Resolve StringResources in composable scope before passing to child composables.
    val aiMessage = stringResource(state.aiMessageRes)
    val aiHighlightPhrases = state.aiHighlightPhrases.map { stringResource(it) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
    ) {
        item {
            GreetingHeader(
                userName = state.userName,
                greetingRes = state.greetingRes,
                onNotificationClick = { onIntent(HomeTabIntent.NotificationClicked) },
            )
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            ActiveSessionBanner(minsUntilStart = state.liveSession.minsUntilStart)
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            LiveSessionCard(
                session = state.liveSession,
                onJoinClick = { onIntent(HomeTabIntent.JoinSessionClicked) },
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            AiCompanionCard(
                message = aiMessage,
                highlightPhrases = aiHighlightPhrases,
                onQuizClick = { onIntent(HomeTabIntent.QuickQuizClicked) },
                onFormulaClick = { onIntent(HomeTabIntent.FormulaSheetClicked) },
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            ConceptMasterySection(
                subjects = state.subjects,
                onBreakdownClick = { onIntent(HomeTabIntent.BreakdownClicked) },
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            UpcomingSessionsSection(
                sessions = state.upcomingSessions,
                onViewCalendarClick = { onIntent(HomeTabIntent.ViewCalendarClicked) },
            )
        }
        // Extra space so content clears the sticky credits banner (~76dp) + NavigationBar padding.
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
