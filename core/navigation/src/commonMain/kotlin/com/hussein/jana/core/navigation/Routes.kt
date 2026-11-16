package com.hussein.jana.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute : NavKey

sealed interface AuthRoute : NavKey {
    @Serializable data object Login : AuthRoute
    @Serializable data object Register : AuthRoute
}

sealed interface HomeRoute : NavKey {
    @Serializable data class Root(val initialTab: String = "HOME") : HomeRoute
}
