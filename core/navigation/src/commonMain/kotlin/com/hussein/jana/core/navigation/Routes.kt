package com.hussein.jana.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute : NavKey

sealed interface AuthRoute : NavKey {
    @Serializable data class Login(val initialTab: String = "STUDENT") : AuthRoute
    @Serializable data object Register : AuthRoute
}

@Serializable
data object ForgotPasswordRoute : NavKey

sealed interface HomeRoute : NavKey {
    @Serializable data class Root(val initialTab: String = "HOME") : HomeRoute
}
