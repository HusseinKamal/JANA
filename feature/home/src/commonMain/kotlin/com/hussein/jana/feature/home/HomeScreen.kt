package com.hussein.jana.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.home_title
import com.hussein.jana.core.designsystem.tab_discover
import com.hussein.jana.core.designsystem.tab_home
import com.hussein.jana.core.designsystem.tab_profile
import com.hussein.jana.core.designsystem.tab_wishlist
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeFlow() {
    val viewModel: HomeViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    HomeScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun HomeScreen(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
) {
    val tabs = listOf(
        stringResource(Res.string.tab_home) to Icons.Rounded.Home,
        stringResource(Res.string.tab_discover) to Icons.Rounded.Search,
        stringResource(Res.string.tab_wishlist) to Icons.Rounded.Favorite,
        stringResource(Res.string.tab_profile) to Icons.Rounded.Person,
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, (label, icon) ->
                    NavigationBarItem(
                        selected = state.selectedTab == index,
                        onClick = { onIntent(HomeIntent.SelectTab(index)) },
                        icon = { Icon(imageVector = icon, contentDescription = label) },
                        label = { Text(label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.home_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
