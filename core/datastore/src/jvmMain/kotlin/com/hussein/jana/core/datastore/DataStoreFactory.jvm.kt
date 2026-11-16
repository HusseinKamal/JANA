package com.hussein.jana.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath
import java.io.File

actual fun createDataStore(): DataStore<Preferences> {
    val dir = File(System.getProperty("user.home"), ".jana").also { it.mkdirs() }
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { File(dir, DATASTORE_FILE_NAME).absolutePath.toPath() }
    )
}
