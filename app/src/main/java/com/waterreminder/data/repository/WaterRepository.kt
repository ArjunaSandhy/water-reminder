package com.waterreminder.data.repository

import com.waterreminder.data.local.dao.WaterEntryDao
import com.waterreminder.data.local.entity.WaterEntry
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WaterRepository @Inject constructor(
    private val waterEntryDao: WaterEntryDao
) {
    fun getTotalByDate(date: String): Flow<Int?> {
        return waterEntryDao.getTotalByDate(date)
    }
    
    fun getEntriesByDate(date: String): Flow<List<WaterEntry>> {
        return waterEntryDao.getEntriesByDate(date)
    }
    
    fun getAllDates(): Flow<List<String>> {
        return waterEntryDao.getAllDates()
    }
    
    suspend fun addWaterEntry(amountMl: Int, note: String?, date: String): Long {
        val entry = WaterEntry(
            amountMl = amountMl,
            note = note?.takeIf { it.isNotBlank() }?.take(200),
            date = date
        )
        return waterEntryDao.insertEntry(entry)
    }
    
    suspend fun deleteEntry(id: Long) {
        waterEntryDao.deleteEntry(id)
    }
    
    suspend fun getEntryById(id: Long): WaterEntry? {
        return waterEntryDao.getEntryById(id)
    }
    
    suspend fun getTotalByDateSuspend(date: String): Int {
        return waterEntryDao.getTotalByDateSuspend(date) ?: 0
    }
}
