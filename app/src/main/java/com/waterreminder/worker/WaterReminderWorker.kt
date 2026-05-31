package com.waterreminder.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.waterreminder.MainActivity
import com.waterreminder.data.repository.WaterRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

@HiltWorker
class WaterReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: WaterRepository
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            // Check if reminder is enabled
            val isEnabled = repository.reminderEnabled.first()
            if (!isEnabled) {
                return Result.success()
            }
            
            // Check if current time is within active hours
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val currentMin = Calendar.getInstance().get(Calendar.MINUTE)
            val currentTimeMinutes = currentHour * 60 + currentMin
            
            val startHour = repository.reminderStartHour.first()
            val startMin = repository.reminderStartMin.first()
            val startTimeMinutes = startHour * 60 + startMin
            
            val endHour = repository.reminderEndHour.first()
            val endMin = repository.reminderEndMin.first()
            val endTimeMinutes = endHour * 60 + endMin
            
            if (currentTimeMinutes < startTimeMinutes || currentTimeMinutes > endTimeMinutes) {
                return Result.success()
            }
            
            // Check if target is already reached
            val todayTotal = repository.getTodayTotal().first() ?: 0
            val target = repository.dailyTargetMl.first()
            
            if (todayTotal >= target) {
                return Result.success()
            }
            
            // Send notification
            sendNotification()
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
    
    private fun sendNotification() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create notification channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Pengingat untuk minum air"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        // Create intent to open app
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Build notification
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Use default icon for now
            .setContentTitle("Waktunya minum air! 💧")
            .setContentText("Jangan lupa minum air untuk menjaga tubuh tetap sehat.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    companion object {
        private const val CHANNEL_ID = "water_reminder_channel"
        private const val CHANNEL_NAME = "Pengingat Minum Air"
        private const val NOTIFICATION_ID = 1001
    }
}
