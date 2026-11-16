package com.hussein.jana.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

internal const val DATASTORE_FILE_NAME = "jana_preferences.preferences_pb"

expect fun createDataStore(): DataStore<Preferences>
