package com.hussein.jana.core.di

import com.hussein.jana.core.datastore.AppPreferences
import com.hussein.jana.core.datastore.SecureStorage
import com.hussein.jana.core.datastore.TokenRepository
import com.hussein.jana.core.datastore.createDataStore
import com.hussein.jana.core.database.createDatabase
import com.hussein.jana.core.navigation.AppNavigator
import com.hussein.jana.core.network.createHttpClient
import org.koin.dsl.module

val coreModule = module {
    single { createHttpClient() }
    single { AppNavigator() }
    single { createDatabase() }
    single { createDataStore() }
    single { AppPreferences(get()) }
    single { SecureStorage() }
    single { TokenRepository(get()) }
}
