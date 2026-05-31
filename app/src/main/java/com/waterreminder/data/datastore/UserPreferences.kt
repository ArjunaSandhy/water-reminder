package com.waterreminder.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserSettings(
    val dailyTargetMl: Int = 2000,
    val unit: String = "ml",
    val reminderEnabled: Boolean = false,
    val reminderIntervalMin: Int = 60,
    val reminderStartHour: Int = 7,
    val reminderStartMin: Int = 0,
    val reminderEndHour: Int = 22,
    val reminderEndMin: Int = 0
) {
    fun getDisplayAmount(amountMl: Int): String {
        return if (unit == "oz") {
            val oz = amountMl / 29.574
            String.format("%.1f oz", oz)
        } else {
            "$amountMl ml"
        }
    }
    
    fun getDisplayTarget(): String {
        return getDisplayAmount(dailyTargetMl)
    }
    
    fun mlToDisplayUnit(amountMl: Int): Double {
        return if (unit == "oz") {
            amountMl / 29.574
        } else {
            amountMl.toDouble()
        }
    }
    
    fun displayUnitToMl(amount: Double): Int {
        return if (unit == "oz") {
            (amount * 29.574).toInt()
        } else {
            amount.toInt()
        }
    }
}

@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val DAILY_TARGET_ML = intPreferencesKey("daily_target_ml")
        val UNIT = stringPreferencesKey("unit")
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_INTERVAL_MIN = intPreferencesKey("reminder_interval_min")
        val REMINDER_START_HOUR = intPreferencesKey("reminder_start_hour")
        val REMINDER_START_MIN = intPreferencesKey("reminder_start_min")
        val REMINDER_END_HOUR = intPreferencesKey("reminder_end_hour")
        val REMINDER_END_MIN = intPreferencesKey("reminder_end_min")
    }
    
    val userSettings: Flow<UserSettings> = dataStore.data.map { preferences ->
        UserSettings(
            dailyTargetMl = preferences[DAILY_TARGET_ML] ?: 2000,
            unit = preferences[UNIT] ?: "ml",
            reminderEnabled = preferences[REMINDER_ENABLED] ?: false,
            reminderIntervalMin = preferences[REMINDER_INTERVAL_MIN] ?: 60,
            reminderStartHour = preferences[REMINDER_START_HOUR] ?: 7,
            reminderStartMin = preferences[REMINDER_START_MIN] ?: 0,
            reminderEndHour = preferences[REMINDER_END_HOUR] ?: 22,
            reminderEndMin = preferences[REMINDER_END_MIN] ?: 0
        )
    }
    
    suspend fun setDailyTarget(targetMl: Int) {
        val validTarget = targetMl.coerceIn(500, 5000)
        dataStore.edit { preferences ->
            preferences[DAILY_TARGET_ML] = validTarget
        }
    }
    
    suspend fun setUnit(unit: String) {
        val validUnit = if (unit in listOf("ml", "oz")) unit else "ml"
        dataStore.edit { preferences ->
            preferences[UNIT] = validUnit
        }
    }
    
    suspend fun setReminderEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[REMINDER_ENABLED] = enabled
        }
    }
    
    suspend fun setReminderInterval(intervalMin: Int) {
        val validInterval = if (intervalMin in listOf(30, 60, 90, 120)) intervalMin else 60
        dataStore.edit { preferences ->
            preferences[REMINDER_INTERVAL_MIN] = validInterval
        }
    }
    
    suspend fun setReminderStartTime(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[REMINDER_START_HOUR] = hour.coerceIn(0, 23)
            preferences[REMINDER_START_MIN] = minute.coerceIn(0, 59)
        }
    }
    
    suspend fun setReminderEndTime(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[REMINDER_END_HOUR] = hour.coerceIn(0, 23)
            preferences[REMINDER_END_MIN] = minute.coerceIn(0, 59)
        }
    }
}
