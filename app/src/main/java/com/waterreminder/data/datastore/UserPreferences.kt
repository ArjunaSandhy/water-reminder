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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {
    
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
    
    val dailyTargetMl: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[DAILY_TARGET_ML] ?: 2000 }
    
    val unit: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[UNIT] ?: "ml" }
    
    val reminderEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[REMINDER_ENABLED] ?: false }
    
    val reminderIntervalMin: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[REMINDER_INTERVAL_MIN] ?: 60 }
    
    val reminderStartHour: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[REMINDER_START_HOUR] ?: 7 }
    
    val reminderStartMin: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[REMINDER_START_MIN] ?: 0 }
    
    val reminderEndHour: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[REMINDER_END_HOUR] ?: 22 }
    
    val reminderEndMin: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[REMINDER_END_MIN] ?: 0 }
    
    suspend fun setDailyTarget(targetMl: Int) {
        context.dataStore.edit { preferences ->
            preferences[DAILY_TARGET_ML] = targetMl
        }
    }
    
    suspend fun setUnit(unit: String) {
        context.dataStore.edit { preferences ->
            preferences[UNIT] = unit
        }
    }
    
    suspend fun setReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REMINDER_ENABLED] = enabled
        }
    }
    
    suspend fun setReminderInterval(intervalMin: Int) {
        context.dataStore.edit { preferences ->
            preferences[REMINDER_INTERVAL_MIN] = intervalMin
        }
    }
    
    suspend fun setReminderStartTime(hour: Int, minute: Int) {
        context.dataStore.edit { preferences ->
            preferences[REMINDER_START_HOUR] = hour
            preferences[REMINDER_START_MIN] = minute
        }
    }
    
    suspend fun setReminderEndTime(hour: Int, minute: Int) {
        context.dataStore.edit { preferences ->
            preferences[REMINDER_END_HOUR] = hour
            preferences[REMINDER_END_MIN] = minute
        }
    }
}
