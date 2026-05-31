package com.waterreminder.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

/**
 * Worker that schedules itself for the next midnight.
 * Note: No data deletion is needed since Room queries filter by date.
 * This worker is mainly for any midnight reset logic if needed in the future.
 */
@HiltWorker
class MidnightResetWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            // Schedule the next midnight worker
            scheduleNextMidnight(context)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
    
    companion object {
        const val WORK_NAME = "midnight_reset_work"
        
        fun scheduleNextMidnight(context: Context) {
            val now = LocalDateTime.now()
            val nextMidnight = now.toLocalDate().plusDays(1).atTime(LocalTime.MIDNIGHT)
            val delayMillis = Duration.between(now, nextMidnight).toMillis()
            
            val workRequest = OneTimeWorkRequestBuilder<MidnightResetWorker>()
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .build()
            
            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }
}
