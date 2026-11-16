package com.hussein.jana.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toOkioPath

private lateinit var _context: Context

fun initDataStoreContext(context: Context) {
    _context = context.applicationContext
}

actual fun createDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            _context.filesDir.resolve(DATASTORE_FILE_NAME).toOkioPath()
        }
    )
