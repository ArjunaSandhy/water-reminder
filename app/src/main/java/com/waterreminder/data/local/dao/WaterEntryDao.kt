package com.waterreminder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.waterreminder.data.local.entity.WaterEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterEntryDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: WaterEntry): Long
    
    @Query("SELECT SUM(amount_ml) FROM water_entries WHERE date = :date")
    fun getTotalByDate(date: String): Flow<Int?>
    
    @Query("SELECT * FROM water_entries WHERE date = :date ORDER BY timestamp DESC")
    fun getEntriesByDate(date: String): Flow<List<WaterEntry>>
    
    @Query("SELECT DISTINCT date FROM water_entries ORDER BY date DESC")
    fun getAllDates(): Flow<List<String>>
    
    @Query("DELETE FROM water_entries WHERE id = :id")
    suspend fun deleteEntry(id: Long)
    
    @Query("SELECT * FROM water_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): WaterEntry?
    
    @Query("SELECT SUM(amount_ml) FROM water_entries WHERE date = :date")
    suspend fun getTotalByDateSuspend(date: String): Int?
}
