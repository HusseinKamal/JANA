package com.hussein.jana.feature.splash.di

import com.hussein.jana.core.navigation.FeatureEntryProvider
import com.hussein.jana.feature.splash.SplashEntryProvider
import com.hussein.jana.feature.splash.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val splashModule = module {
    viewModel { SplashViewModel(tokenRepository = get()) }
    single<FeatureEntryProvider>(named("SplashEntryProvider")) { SplashEntryProvider() }
}
