package com.waterreminder.data.repository

import com.waterreminder.data.datastore.UserPreferences
import com.waterreminder.data.local.WaterDao
import com.waterreminder.data.local.WaterEntry
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WaterRepository @Inject constructor(
    private val waterDao: WaterDao,
    private val userPreferences: UserPreferences
) {
    
    // Room operations
    suspend fun addWaterEntry(amountMl: Int, note: String?): Long {
        val today = getCurrentDate()
        val entry = WaterEntry(
            amountMl = amountMl,
            note = note,
            timestamp = System.currentTimeMillis(),
            date = today
        )
        return waterDao.insertEntry(entry)
    }
    
    fun getTotalByDate(date: String): Flow<Int?> {
        return waterDao.getTotalByDate(date)
    }
    
    fun getTodayTotal(): Flow<Int?> {
        return waterDao.getTotalByDate(getCurrentDate())
    }
    
    fun getEntriesByDate(date: String): Flow<List<WaterEntry>> {
        return waterDao.getEntriesByDate(date)
    }
    
    fun getTodayEntries(): Flow<List<WaterEntry>> {
        return waterDao.getEntriesByDate(getCurrentDate())
    }
    
    fun getAllDates(): Flow<List<String>> {
        return waterDao.getAllDates()
    }
    
    suspend fun deleteEntry(id: Long) {
        waterDao.deleteEntry(id)
    }
    
    suspend fun getLatestEntry(): WaterEntry? {
        return waterDao.getLatestEntry()
    }
    
    suspend fun getEntryCountByDate(date: String): Int {
        return waterDao.getEntryCountByDate(date)
    }
    
    // DataStore operations - Settings
    val dailyTargetMl: Flow<Int> = userPreferences.dailyTargetMl
    val unit: Flow<String> = userPreferences.unit
    
    suspend fun setDailyTarget(targetMl: Int) {
        userPreferences.setDailyTarget(targetMl)
    }
    
    suspend fun setUnit(unit: String) {
        userPreferences.setUnit(unit)
    }
    
    // DataStore operations - Reminder
    val reminderEnabled: Flow<Boolean> = userPreferences.reminderEnabled
    val reminderIntervalMin: Flow<Int> = userPreferences.reminderIntervalMin
    val reminderStartHour: Flow<Int> = userPreferences.reminderStartHour
    val reminderStartMin: Flow<Int> = userPreferences.reminderStartMin
    val reminderEndHour: Flow<Int> = userPreferences.reminderEndHour
    val reminderEndMin: Flow<Int> = userPreferences.reminderEndMin
    
    suspend fun setReminderEnabled(enabled: Boolean) {
        userPreferences.setReminderEnabled(enabled)
    }
    
    suspend fun setReminderInterval(intervalMin: Int) {
        userPreferences.setReminderInterval(intervalMin)
    }
    
    suspend fun setReminderStartTime(hour: Int, minute: Int) {
        userPreferences.setReminderStartTime(hour, minute)
    }
    
    suspend fun setReminderEndTime(hour: Int, minute: Int) {
        userPreferences.setReminderEndTime(hour, minute)
    }
    
    // Utility
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
    
    fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
}
