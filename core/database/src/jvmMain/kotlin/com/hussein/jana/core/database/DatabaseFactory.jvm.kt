package com.hussein.jana.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabaseBuilder(): RoomDatabase.Builder<JanaDatabase> {
    val dbFile = File(System.getProperty("user.home"), ".jana/jana.db")
    dbFile.parentFile?.mkdirs()
    return Room.databaseBuilder<JanaDatabase>(name = dbFile.absolutePath)
}
