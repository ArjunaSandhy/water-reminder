package com.waterreminder.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.waterreminder.data.datastore.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Check if reminders are enabled and reschedule
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val dataStore = context.dataStore
                    val reminderEnabled = dataStore.data.map { preferences ->
                        preferences[com.waterreminder.data.datastore.UserPreferences.REMINDER_ENABLED] ?: false
                    }.first()
                    
                    val intervalMin = dataStore.data.map { preferences ->
                        preferences[com.waterreminder.data.datastore.UserPreferences.REMINDER_INTERVAL_MIN] ?: 60
                    }.first()
                    
                    if (reminderEnabled) {
                        val workRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(
                            intervalMin.toLong(), TimeUnit.MINUTES
                        ).build()
                        
                        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                            WaterReminderWorker.WORK_NAME,
                            ExistingPeriodicWorkPolicy.KEEP,
                            workRequest
                        )
                    }
                } catch (e: Exception) {
                    // Handle error silently
                }
            }
        }
    }
}
