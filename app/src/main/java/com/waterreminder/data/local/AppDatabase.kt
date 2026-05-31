package com.waterreminder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.waterreminder.data.local.dao.WaterEntryDao
import com.waterreminder.data.local.entity.WaterEntry

@Database(
    entities = [WaterEntry::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterEntryDao(): WaterEntryDao
    
    companion object {
        const val DATABASE_NAME = "water_reminder_db"
    }
}
