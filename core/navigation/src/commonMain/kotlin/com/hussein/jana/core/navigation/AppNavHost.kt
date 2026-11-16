package com.hussein.jana.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.koinInject

@Composable
fun AppNavHost(
    providers: List<FeatureEntryProvider>,
    startDestination: NavKey = SplashRoute,
    navigator: AppNavigator = koinInject(),
) {
    val backStack = remember { mutableStateListOf<NavKey>(startDestination) }

    LaunchedEffect(Unit) {
        navigator.commands.collect { command ->
            when (command) {
                is NavCommand.To -> backStack.add(command.route)
                NavCommand.Back -> {
                    if (backStack.size > 1) backStack.removeAt(backStack.size - 1)
                }
                is NavCommand.PopTo -> {
                    val index = backStack.indexOfLast { it == command.route }
                    if (index != -1) {
                        val removeCount = backStack.size - (if (command.inclusive) index else index + 1)
                        repeat(removeCount) { backStack.removeAt(backStack.size - 1) }
                    }
                }
                is NavCommand.ReplaceAll -> {
                    backStack.clear()
                    backStack.add(command.route)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavDisplay(
            backStack = backStack,
            onBack = { navigator.back() },
            entryProvider = { key ->
                @Suppress("UNCHECKED_CAST")
                (providers.firstNotNullOfOrNull { it.entryFor(key) }
                    ?: NavEntry(key) {}) as NavEntry<NavKey>
            },
        )
    }
}
