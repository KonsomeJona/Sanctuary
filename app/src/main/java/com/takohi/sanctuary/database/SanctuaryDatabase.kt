package com.takohi.sanctuary.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Application::class, SystemApplication::class], version = 1, exportSchema = false)
abstract class SanctuaryDatabase : RoomDatabase() {
    companion object {
        @Volatile private var INSTANCE: SanctuaryDatabase? = null

        fun getInstance(context: Context): SanctuaryDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        SanctuaryDatabase::class.java, "sanctuary-database.db")
                        .build()
    }

    abstract fun applicationDao(): ApplicationDao

    abstract fun systemApplicationDao(): SystemApplicationDao
}