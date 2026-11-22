package com.hussein.jana.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.VellumCanvas
import com.hussein.jana.core.designsystem.app_name
import com.hussein.jana.core.designsystem.tab_courses
import com.hussein.jana.core.designsystem.tab_jana_ai
import com.hussein.jana.core.designsystem.tab_live_lessons
import com.hussein.jana.core.designsystem.tab_my_teacher_chat
import com.hussein.jana.core.designsystem.tab_profile
import com.hussein.jana.feature.home.tabs.chat.MyTeacherChatTabScreen
import com.hussein.jana.feature.home.tabs.courses.CoursesTabScreen
import com.hussein.jana.feature.home.tabs.janaai.JanaAiTabScreen
import com.hussein.jana.feature.home.tabs.live.LiveLessonsTabScreen
import com.hussein.jana.feature.home.tabs.profile.ProfileTabScreen
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
                HomeTab.JANA_AI  -> stringResource(Res.string.tab_jana_ai)
                HomeTab.COURSES  -> stringResource(Res.string.tab_courses)
                else             -> stringResource(Res.string.app_name)
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
                HomeTab.LIVE_LESSONS    -> LiveLessonsTabScreen()
                HomeTab.MY_TEACHER_CHAT -> MyTeacherChatTabScreen()
                HomeTab.JANA_AI         -> JanaAiTabScreen()
                HomeTab.COURSES         -> CoursesTabScreen()
                HomeTab.PROFILE         -> ProfileTabScreen()
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
        indicatorColor      = MaterialTheme.colorScheme.primary,
        selectedIconColor   = Color.White,
        selectedTextColor   = MaterialTheme.colorScheme.primary,
        unselectedIconColor = OnSurfaceVariant,
        unselectedTextColor = OnSurfaceVariant,
    )

    NavigationBar {
        // (1) Live Lessons
        NavigationBarItem(
            selected = selectedTab == HomeTab.LIVE_LESSONS,
            onClick = { onTabSelected(HomeTab.LIVE_LESSONS) },
            icon = { Icon(Icons.Rounded.Videocam, contentDescription = null) },
            label = { Text(stringResource(Res.string.tab_live_lessons)) },
            colors = itemColors,
        )
        // (2) My Teacher Chat
        NavigationBarItem(
            selected = selectedTab == HomeTab.MY_TEACHER_CHAT,
            onClick = { onTabSelected(HomeTab.MY_TEACHER_CHAT) },
            icon = { Icon(Icons.AutoMirrored.Rounded.Chat, contentDescription = null) },
            label = { Text(stringResource(Res.string.tab_my_teacher_chat)) },
            colors = itemColors,
        )
        // (3) JANA AI
        NavigationBarItem(
            selected = selectedTab == HomeTab.JANA_AI,
            onClick = { onTabSelected(HomeTab.JANA_AI) },
            icon = { Icon(Icons.Rounded.AutoAwesome, contentDescription = null) },
            label = { Text(stringResource(Res.string.tab_jana_ai)) },
            colors = itemColors,
        )
        // (4) Courses
        NavigationBarItem(
            selected = selectedTab == HomeTab.COURSES,
            onClick = { onTabSelected(HomeTab.COURSES) },
            icon = { Icon(Icons.Rounded.MenuBook, contentDescription = null) },
            label = { Text(stringResource(Res.string.tab_courses)) },
            colors = itemColors,
        )
        // (5) Profile
        NavigationBarItem(
            selected = selectedTab == HomeTab.PROFILE,
            onClick = { onTabSelected(HomeTab.PROFILE) },
            icon = { Icon(Icons.Rounded.Person, contentDescription = null) },
            label = { Text(stringResource(Res.string.tab_profile)) },
            colors = itemColors,
        )
    }
}
