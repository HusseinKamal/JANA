package com.hussein.jana.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hussein.jana.core.database.dao.UserDao
import com.hussein.jana.core.database.entities.UserEntity

// Note: @ConstructedBy / RoomDatabaseConstructor requires KSP code generation.
// KSP is deferred until a KSP version compatible with com.android.kotlin.multiplatform.library
// (AGP 9.1.0+) is available. The database structure is fully defined here.
@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class JanaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
