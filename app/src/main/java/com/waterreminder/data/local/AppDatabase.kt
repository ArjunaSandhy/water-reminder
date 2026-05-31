package com.waterreminder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [WaterEntry::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterDao(): WaterDao
    
    companion object {
        const val DATABASE_NAME = "water_reminder_db"
    }
}
