package com.waterreminder.ui.reminder

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.waterreminder.data.datastore.UserPreferences
import com.waterreminder.worker.WaterReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class ReminderUiState(
    val isEnabled: Boolean = false,
    val intervalMin: Int = 60,
    val startHour: Int = 7,
    val startMin: Int = 0,
    val endHour: Int = 22,
    val endMin: Int = 0,
    val hasNotificationPermission: Boolean = true,
    val showPermissionRationale: Boolean = false,
    val showPermissionDeniedMessage: Boolean = false
) {
    val startTimeDisplay: String
        get() = String.format("%02d:%02d", startHour, startMin)
    
    val endTimeDisplay: String
        get() = String.format("%02d:%02d", endHour, endMin)
    
    val intervalOptions: List<Int> = listOf(30, 60, 90, 120)
    
    fun getIntervalDisplay(minutes: Int): String {
        return when {
            minutes < 60 -> "$minutes menit"
            minutes == 60 -> "1 jam"
            else -> "${minutes / 60} jam ${if (minutes % 60 > 0) "${minutes % 60} menit" else ""}"
        }
    }
}

@HiltViewModel
class ReminderViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferences: UserPreferences,
    private val workManager: WorkManager
) : ViewModel() {
    
    private val _permissionState = MutableStateFlow(
        PermissionState(
            hasPermission = checkNotificationPermission(),
            showRationale = false,
            showDeniedMessage = false
        )
    )
    
    val uiState: StateFlow<ReminderUiState> = combine(
        userPreferences.userSettings,
        _permissionState
    ) { settings, permState ->
        ReminderUiState(
            isEnabled = settings.reminderEnabled,
            intervalMin = settings.reminderIntervalMin,
            startHour = settings.reminderStartHour,
            startMin = settings.reminderStartMin,
            endHour = settings.reminderEndHour,
            endMin = settings.reminderEndMin,
            hasNotificationPermission = permState.hasPermission,
            showPermissionRationale = permState.showRationale,
            showPermissionDeniedMessage = permState.showDeniedMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReminderUiState()
    )
    
    private data class PermissionState(
        val hasPermission: Boolean,
        val showRationale: Boolean,
        val showDeniedMessage: Boolean
    )
    
    fun setReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled && !checkNotificationPermission()) {
                _permissionState.update { it.copy(showRationale = true) }
                return@launch
            }
            
            userPreferences.setReminderEnabled(enabled)
            
            if (enabled) {
                scheduleReminder()
            } else {
                cancelReminder()
            }
        }
    }
    
    fun setInterval(intervalMin: Int) {
        viewModelScope.launch {
            userPreferences.setReminderInterval(intervalMin)
            if (uiState.value.isEnabled) {
                scheduleReminder()
            }
        }
    }
    
    fun setStartTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            userPreferences.setReminderStartTime(hour, minute)
        }
    }
    
    fun setEndTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            userPreferences.setReminderEndTime(hour, minute)
        }
    }
    
    fun onPermissionResult(granted: Boolean) {
        _permissionState.update { 
            it.copy(
                hasPermission = granted,
                showRationale = false,
                showDeniedMessage = !granted
            )
        }
        
        if (granted) {
            viewModelScope.launch {
                userPreferences.setReminderEnabled(true)
                scheduleReminder()
            }
        }
    }
    
    fun dismissRationale() {
        _permissionState.update { it.copy(showRationale = false) }
    }
    
    fun dismissDeniedMessage() {
        _permissionState.update { it.copy(showDeniedMessage = false) }
    }
    
    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
    
    fun refreshPermissionStatus() {
        _permissionState.update { 
            it.copy(hasPermission = checkNotificationPermission())
        }
    }
    
    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    
    private fun scheduleReminder() {
        val intervalMinutes = uiState.value.intervalMin.toLong()
        
        val workRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(
            intervalMinutes, TimeUnit.MINUTES
        ).build()
        
        workManager.enqueueUniquePeriodicWork(
            WaterReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
    
    private fun cancelReminder() {
        workManager.cancelUniqueWork(WaterReminderWorker.WORK_NAME)
    }
}
