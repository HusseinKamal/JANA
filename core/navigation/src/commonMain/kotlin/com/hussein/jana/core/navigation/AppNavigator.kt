package com.hussein.jana.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

sealed interface NavCommand {
    data class To(val route: NavKey) : NavCommand
    data object Back : NavCommand
    data class PopTo(val route: NavKey, val inclusive: Boolean = false) : NavCommand
    data class ReplaceAll(val route: NavKey) : NavCommand
}

class AppNavigator {
    private val _commands = Channel<NavCommand>(Channel.UNLIMITED)
    val commands: Flow<NavCommand> = _commands.receiveAsFlow()

    fun navigate(route: NavKey) { _commands.trySend(NavCommand.To(route)) }
    fun back() { _commands.trySend(NavCommand.Back) }
    fun popTo(route: NavKey, inclusive: Boolean = false) {
        _commands.trySend(NavCommand.PopTo(route, inclusive))
    }
    fun replaceAll(route: NavKey) { _commands.trySend(NavCommand.ReplaceAll(route)) }
}
