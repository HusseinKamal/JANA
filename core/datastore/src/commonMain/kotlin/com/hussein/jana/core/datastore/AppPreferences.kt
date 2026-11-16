package com.hussein.jana.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences(private val dataStore: DataStore<Preferences>) {

    private val KEY_LANGUAGE = stringPreferencesKey("language")
    private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
    private val KEY_ONBOARDED = booleanPreferencesKey("onboarded")

    val language: Flow<String> = dataStore.data.map { it[KEY_LANGUAGE] ?: "en" }
    val isDarkMode: Flow<Boolean> = dataStore.data.map { it[KEY_DARK_MODE] ?: false }
    val isOnboarded: Flow<Boolean> = dataStore.data.map { it[KEY_ONBOARDED] ?: false }

    suspend fun setLanguage(code: String) {
        dataStore.edit { it[KEY_LANGUAGE] = code }
    }

    suspend fun setDarkMode(isDark: Boolean) {
        dataStore.edit { it[KEY_DARK_MODE] = isDark }
    }

    suspend fun setOnboarded(onboarded: Boolean) {
        dataStore.edit { it[KEY_ONBOARDED] = onboarded }
    }
}
