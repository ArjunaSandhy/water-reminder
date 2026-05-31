package com.waterreminder.worker

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.waterreminder.MainActivity
import com.waterreminder.R
import com.waterreminder.WaterReminderApp
import com.waterreminder.data.datastore.UserPreferences
import com.waterreminder.data.repository.WaterRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@HiltWorker
class WaterReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userPreferences: UserPreferences,
    private val waterRepository: WaterRepository
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            val settings = userPreferences.userSettings.first()
            
            // Check if reminders are enabled
            if (!settings.reminderEnabled) {
                return Result.success()
            }
            
            // Check current time against reminder window
            val currentTime = LocalTime.now()
            val startTime = LocalTime.of(settings.reminderStartHour, settings.reminderStartMin)
            val endTime = LocalTime.of(settings.reminderEndHour, settings.reminderEndMin)
            
            if (currentTime.isBefore(startTime) || currentTime.isAfter(endTime)) {
                // Outside reminder window, skip
                return Result.success()
            }
            
            // Check if daily target is already achieved
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val totalToday = waterRepository.getTotalByDateSuspend(today)
            
            if (totalToday >= settings.dailyTargetMl) {
                // Target achieved, skip notification
                return Result.success()
            }
            
            // Check notification permission for Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                
                if (!hasPermission) {
                    return Result.success()
                }
            }
            
            // Send notification
            sendNotification()
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
    
    private fun sendNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, WaterReminderApp.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    companion object {
        const val WORK_NAME = "water_reminder_work"
        private const val NOTIFICATION_ID = 1001
    }
}
