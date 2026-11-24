package com.hussein.jana.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelStore
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.koinInject

/**
 * Navigation state that must outlive [AppNavHost]: the back stack, the per-entry
 * [ViewModelStore]s and the [SaveableStateHolder].
 *
 * The app root wraps [AppNavHost] in `key(language)` so a runtime language switch rebuilds
 * the tree and Compose resources reload their strings. Creating this state above that key
 * keeps the current screen, its ViewModels and its saved UI state across the rebuild.
 */
@Stable
class AppNavState internal constructor(
    val backStack: SnapshotStateList<NavKey>,
    internal val viewModelStores: MutableMap<Any, ViewModelStore>,
    internal val saveableStateHolder: SaveableStateHolder,
)

/** Remembers an [AppNavState]; all ViewModel stores are cleared when it leaves composition. */
@Composable
fun rememberAppNavState(startDestination: NavKey): AppNavState {
    val backStack = remember { mutableStateListOf(startDestination) }
    val viewModelStores = remember { mutableMapOf<Any, ViewModelStore>() }
    val saveableStateHolder = rememberSaveableStateHolder()

    DisposableEffect(viewModelStores) {
        onDispose {
            viewModelStores.values.forEach { it.clear() }
            viewModelStores.clear()
        }
    }

    return remember(backStack, viewModelStores, saveableStateHolder) {
        AppNavState(backStack, viewModelStores, saveableStateHolder)
    }
}

@Composable
fun AppNavHost(
    providers: List<FeatureEntryProvider>,
    navState: AppNavState,
    navigator: AppNavigator = koinInject(),
) {
    val backStack = navState.backStack

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
            // Give every back-stack entry its own saved state and ViewModelStore.
            // Without this, koinViewModel() is scoped to the Activity, so a detail
            // screen reused the first ViewModel it created and ignored new route
            // arguments (courseId, openCheckout).
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(navState.saveableStateHolder),
                rememberEntryViewModelStoreDecorator(navState.viewModelStores),
            ),
            entryProvider = { key ->
                @Suppress("UNCHECKED_CAST")
                (providers.firstNotNullOfOrNull { it.entryFor(key) }
                    ?: NavEntry(key) {}) as NavEntry<NavKey>
            },
        )
    }
}
