package com.hussein.jana.di

import com.hussein.jana.core.di.coreModule
import com.hussein.jana.core.navigation.FeatureEntryProvider
import com.hussein.jana.feature.home.di.homeModule
import com.hussein.jana.feature.splash.di.splashModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        coreModule,
        splashModule,
        homeModule,
        module {
            single<List<FeatureEntryProvider>> { getAll<FeatureEntryProvider>() }
        },
    )
}
