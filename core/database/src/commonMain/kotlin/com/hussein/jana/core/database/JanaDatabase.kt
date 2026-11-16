package com.hussein.jana.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.hussein.jana.core.database.dao.UserDao
import com.hussein.jana.core.database.entities.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(JanaDatabaseConstructor::class)
abstract class JanaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

@Suppress("KotlinNoActualForExpect")
expect object JanaDatabaseConstructor : RoomDatabaseConstructor<JanaDatabase>
