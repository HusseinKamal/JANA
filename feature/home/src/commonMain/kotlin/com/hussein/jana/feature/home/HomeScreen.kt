package com.hussein.jana.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hussein.jana.core.designsystem.JanaTopBar
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.PrimaryContainer
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.VellumCanvas
import com.hussein.jana.core.designsystem.home_student_label
import com.hussein.jana.core.designsystem.teachers_title
import com.hussein.jana.core.designsystem.nav_home
import com.hussein.jana.core.designsystem.nav_jana_ai
import com.hussein.jana.core.designsystem.nav_progress
import com.hussein.jana.core.designsystem.nav_schedule
import com.hussein.jana.core.designsystem.nav_teachers
import com.hussein.jana.core.designsystem.schedule_title
import com.hussein.jana.feature.home.tabs.home.HomeTabScreen
import com.hussein.jana.feature.home.tabs.janaai.JanaAiTabScreen
import com.hussein.jana.feature.home.tabs.progress.ProgressTabScreen
import com.hussein.jana.feature.home.tabs.schedule.ScheduleTabScreen
import com.hussein.jana.feature.home.tabs.teachers.TeachersTabScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// ── Entry point (stateful) ────────────────────────────────────────────────────

@Composable
fun HomeFlow() {
    val viewModel: HomeViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    HomeScreen(state = state, onIntent = viewModel::onIntent)
}

// ── Stateless host screen ─────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            val topBarTitle = when (state.selectedTab) {
                HomeTab.TEACHERS -> stringResource(Res.string.teachers_title)
                HomeTab.JANA_AI  -> stringResource(Res.string.nav_jana_ai)
                HomeTab.SCHEDULE -> stringResource(Res.string.schedule_title)
                else             -> stringResource(Res.string.home_student_label)
            }
            JanaTopBar(
                screenTitle = topBarTitle,
                credits = state.credits,
                showAvatar = true,
            )
        },
        bottomBar = {
            HomeBottomNav(
                selectedTab = state.selectedTab,
                onTabSelected = { onIntent(HomeIntent.TabSelected(it)) },
            )
        },
        containerColor = VellumCanvas,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (state.selectedTab) {
                HomeTab.HOME     -> HomeTabScreen(credits = state.credits)
                HomeTab.TEACHERS -> TeachersTabScreen()
                HomeTab.JANA_AI  -> JanaAiTabScreen()
                HomeTab.SCHEDULE -> ScheduleTabScreen()
                HomeTab.PROGRESS -> ProgressTabScreen()
            }
        }
    }
}

// ── Bottom navigation ─────────────────────────────────────────────────────────

@Composable
private fun HomeBottomNav(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
) {
    val itemColors = NavigationBarItemDefaults.colors(
        indicatorColor      = ScholarlyTeal,   // deep teal pill on selected tab
        selectedIconColor   = Color.White,     // white icon on the teal pill
        selectedTextColor   = ScholarlyTeal,   // teal label below the pill
        unselectedIconColor = OnSurfaceVariant,
        unselectedTextColor = OnSurfaceVariant,
    )

    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == HomeTab.HOME,
            onClick = { onTabSelected(HomeTab.HOME) },
            icon = { Icon(Icons.Rounded.GridView, contentDescription = null) },
            label = { Text(stringResource(Res.string.nav_home)) },
            colors = itemColors,
        )
        NavigationBarItem(
            selected = selectedTab == HomeTab.TEACHERS,
            onClick = { onTabSelected(HomeTab.TEACHERS) },
            icon = { Icon(Icons.Rounded.School, contentDescription = null) },
            label = { Text(stringResource(Res.string.nav_teachers)) },
            colors = itemColors,
        )
        NavigationBarItem(
            selected = selectedTab == HomeTab.JANA_AI,
            onClick = { onTabSelected(HomeTab.JANA_AI) },
            icon = { Icon(Icons.Rounded.AutoAwesome, contentDescription = null) },
            label = { Text(stringResource(Res.string.nav_jana_ai)) },
            colors = itemColors,
        )
        NavigationBarItem(
            selected = selectedTab == HomeTab.SCHEDULE,
            onClick = { onTabSelected(HomeTab.SCHEDULE) },
            icon = { Icon(Icons.Rounded.CalendarMonth, contentDescription = null) },
            label = { Text(stringResource(Res.string.nav_schedule)) },
            colors = itemColors,
        )
        NavigationBarItem(
            selected = selectedTab == HomeTab.PROGRESS,
            onClick = { onTabSelected(HomeTab.PROGRESS) },
            icon = { Icon(Icons.AutoMirrored.Rounded.TrendingUp, contentDescription = null) },
            label = { Text(stringResource(Res.string.nav_progress)) },
            colors = itemColors,
        )
    }
}
