package com.hussein.jana.feature.auth.di

import com.hussein.jana.core.navigation.FeatureEntryProvider
import com.hussein.jana.feature.auth.AuthEntryProvider
import com.hussein.jana.feature.auth.AuthViewModel
import com.hussein.jana.feature.auth.register.RegisterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authModule = module {
    viewModel { AuthViewModel(tokenRepository = get()) }
    viewModel { RegisterViewModel(tokenRepository = get()) }
    single<FeatureEntryProvider>(named("AuthEntryProvider")) { AuthEntryProvider() }
}
