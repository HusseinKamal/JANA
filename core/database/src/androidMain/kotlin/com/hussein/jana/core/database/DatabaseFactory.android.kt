package com.hussein.jana.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

private lateinit var _context: Context

fun initDatabaseContext(context: Context) {
    _context = context.applicationContext
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<JanaDatabase> =
    Room.databaseBuilder(
        context = _context,
        name = _context.getDatabasePath("jana.db").absolutePath,
    )
