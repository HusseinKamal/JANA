package com.hussein.jana.core.di

import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication

fun initKoin(
    featureModules: List<Module>,
    appDeclaration: KoinAppDeclaration = {},
): KoinApplication = koinApplication {
    appDeclaration()
    modules(listOf(coreModule) + featureModules)
}
