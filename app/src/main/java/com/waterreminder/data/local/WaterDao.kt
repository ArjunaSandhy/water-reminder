package com.waterreminder.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: WaterEntry): Long
    
    @Query("SELECT SUM(amount_ml) FROM water_entry WHERE date = :date")
    fun getTotalByDate(date: String): Flow<Int?>
    
    @Query("SELECT * FROM water_entry WHERE date = :date ORDER BY timestamp DESC")
    fun getEntriesByDate(date: String): Flow<List<WaterEntry>>
    
    @Query("SELECT DISTINCT date FROM water_entry ORDER BY date DESC")
    fun getAllDates(): Flow<List<String>>
    
    @Query("DELETE FROM water_entry WHERE id = :id")
    suspend fun deleteEntry(id: Long)
    
    @Query("SELECT * FROM water_entry ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestEntry(): WaterEntry?
    
    @Query("SELECT COUNT(*) FROM water_entry WHERE date = :date")
    suspend fun getEntryCountByDate(date: String): Int
}
