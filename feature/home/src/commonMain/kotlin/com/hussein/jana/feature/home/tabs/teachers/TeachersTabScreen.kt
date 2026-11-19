package com.hussein.jana.feature.home.tabs.teachers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hussein.jana.core.designsystem.AmberDark
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.SuccessGreen
import com.hussein.jana.core.designsystem.teachers_filters_coming_soon
import com.hussein.jana.core.designsystem.teachers_headline
import com.hussein.jana.core.designsystem.teachers_hub_label
import com.hussein.jana.core.designsystem.teachers_no_results
import com.hussein.jana.core.designsystem.teachers_profile_coming_soon
import com.hussein.jana.core.designsystem.teachers_reserve_coming_soon
import com.hussein.jana.core.designsystem.teachers_title
import com.hussein.jana.core.designsystem.teachers_verified_label
import com.hussein.jana.feature.home.tabs.teachers.components.HowJanaWorksCard
import com.hussein.jana.feature.home.tabs.teachers.components.SubjectFilterRow
import com.hussein.jana.feature.home.tabs.teachers.components.TeacherCard
import com.hussein.jana.feature.home.tabs.teachers.components.TeacherSearchBar
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// ── Entry point (stateful) ────────────────────────────────────────────────────

/**
 * Stateful entry point for the Teachers / Discover tab.
 *
 * Strings are injected into the ViewModel via [LaunchedEffect] using the
 * login-screen pattern so locale changes propagate without recreating the VM.
 */
@Composable
fun TeachersTabScreen() {
    val viewModel: TeachersTabViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Resolve string resources in composable scope and push them into the VM.
    val profileComingSoon = stringResource(Res.string.teachers_profile_coming_soon)
    val reserveComingSoon = stringResource(Res.string.teachers_reserve_coming_soon)
    val filtersComingSoon = stringResource(Res.string.teachers_filters_coming_soon)
    LaunchedEffect(profileComingSoon, reserveComingSoon, filtersComingSoon) {
        viewModel.updateStrings(
            TeachersStrings(
                profileComingSoon = profileComingSoon,
                reserveComingSoon = reserveComingSoon,
                filtersComingSoon = filtersComingSoon,
            ),
        )
    }

    // Collect one-shot effects.
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is TeachersTabEffect.ShowSnackbar ->
                    snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        TeachersTabContent(
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
internal fun TeachersTabContent(
    state: TeachersTabState,
    onIntent: (TeachersTabIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val screenTitle = stringResource(Res.string.teachers_title)

    // Outer Column splits the screen into two zones:
    //   1. Sticky top  — header + search bar + filter chips, never scrolls away
    //   2. Scrollable  — teacher cards + info card, scrolls independently
    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = screenTitle },
    ) {
        // ── Sticky top block ──────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 10.dp, bottom = 6.dp),
        ) {
            TeachersHeader()
            Spacer(modifier = Modifier.height(8.dp))
            TeacherSearchBar(
                query = state.searchQuery,
                onQueryChange = { onIntent(TeachersTabIntent.SearchQueryChanged(it)) },
                onFilterClick = { onIntent(TeachersTabIntent.FilterButtonClicked) },
            )
            Spacer(modifier = Modifier.height(8.dp))
            SubjectFilterRow(
                filters = state.filters,
                selectedKey = state.selectedFilter,
                onFilterSelected = { onIntent(TeachersTabIntent.FilterSelected(it)) },
            )
        }

        // ── Scrollable teacher list ───────────────────────────────────────────
        LazyColumn(modifier = Modifier.weight(1f)) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            if (state.filteredTeachers.isEmpty()) {
                item {
                    EmptyState(modifier = Modifier.padding(horizontal = 16.dp))
                }
            } else {
                items(state.filteredTeachers) { teacher ->
                    TeacherCard(
                        teacher = teacher,
                        onProfileClick = { onIntent(TeachersTabIntent.ProfileClicked(teacher.id)) },
                        onReserveClick = { onIntent(TeachersTabIntent.ReserveClicked(teacher.id)) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            HowJanaWorksCard(
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        // Clear bottom nav bar.
        item { Spacer(modifier = Modifier.height(80.dp)) }
        }   // end LazyColumn
    }       // end outer Column
}

// ── Sub-composables ───────────────────────────────────────────────────────────

@Composable
private fun TeachersHeader(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        // Eyebrow label — tiny amber uppercase
        Text(
            text = stringResource(Res.string.teachers_hub_label).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = AmberDark,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Headline row — reduced from headlineLarge to titleLarge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(Res.string.teachers_headline),
                style = MaterialTheme.typography.titleLarge,
                color = ScholarlyTeal,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )

            Spacer(modifier = Modifier.width(8.dp))

            // "100% Verified" badge — compact
            Surface(
                shape = CircleShape,
                color = SuccessGreen.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.3f)),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(10.dp),
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = stringResource(Res.string.teachers_verified_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessGreen,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.SearchOff,
                contentDescription = null,
                tint = OnSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(48.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(Res.string.teachers_no_results),
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
