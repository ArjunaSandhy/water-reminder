package com.waterreminder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.*
import com.waterreminder.data.datastore.UserPreferences
import com.waterreminder.worker.WaterReminderWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var userPreferences: UserPreferences
    
    @Inject
    lateinit var workManager: WorkManager
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule reminder work after device reboot
            CoroutineScope(Dispatchers.IO).launch {
                val isEnabled = userPreferences.reminderEnabled.first()
                if (isEnabled) {
                    val intervalMin = userPreferences.reminderIntervalMin.first()
                    scheduleReminder(intervalMin)
                }
            }
        }
    }
    
    private fun scheduleReminder(intervalMinutes: Int) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
        
        val workRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(
            intervalMinutes.toLong(),
            TimeUnit.MINUTES,
            15,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag("water_reminder")
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            "water_reminder_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
}
