package com.waterreminder.ui.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.waterreminder.data.repository.WaterRepository
import com.waterreminder.worker.WaterReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
    val isSaving: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val repository: WaterRepository,
    private val workManager: WorkManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ReminderUiState())
    val uiState: StateFlow<ReminderUiState> = _uiState.asStateFlow()
    
    init {
        loadReminderSettings()
    }
    
    private fun loadReminderSettings() {
        viewModelScope.launch {
            combine(
                repository.reminderEnabled,
                repository.reminderIntervalMin,
                repository.reminderStartHour,
                repository.reminderStartMin,
                repository.reminderEndHour,
                repository.reminderEndMin
            ) { enabled, interval, startHour, startMin, endHour, endMin ->
                ReminderUiState(
                    isEnabled = enabled,
                    intervalMin = interval,
                    startHour = startHour,
                    startMin = startMin,
                    endHour = endHour,
                    endMin = endMin
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
    
    fun setEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isEnabled = enabled)
    }
    
    fun setInterval(intervalMin: Int) {
        _uiState.value = _uiState.value.copy(intervalMin = intervalMin)
    }
    
    fun setStartTime(hour: Int, minute: Int) {
        _uiState.value = _uiState.value.copy(startHour = hour, startMin = minute)
    }
    
    fun setEndTime(hour: Int, minute: Int) {
        _uiState.value = _uiState.value.copy(endHour = hour, endMin = minute)
    }
    
    fun saveSettings() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(isSaving = true, errorMessage = null)
        
        viewModelScope.launch {
            try {
                // Save to DataStore
                repository.setReminderEnabled(currentState.isEnabled)
                repository.setReminderInterval(currentState.intervalMin)
                repository.setReminderStartTime(currentState.startHour, currentState.startMin)
                repository.setReminderEndTime(currentState.endHour, currentState.endMin)
                
                // Schedule or cancel WorkManager
                if (currentState.isEnabled) {
                    scheduleReminder(currentState.intervalMin)
                } else {
                    cancelReminder()
                }
                
                _uiState.value = currentState.copy(
                    isSaving = false,
                    successMessage = "Pengaturan berhasil disimpan"
                )
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isSaving = false,
                    errorMessage = "Gagal menyimpan: ${e.message}"
                )
            }
        }
    }
    
    private fun scheduleReminder(intervalMinutes: Int) {
        // Cancel existing work
        workManager.cancelUniqueWork(REMINDER_WORK_NAME)
        
        // Create constraints
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
        
        // Create periodic work request
        val workRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(
            intervalMinutes.toLong(),
            TimeUnit.MINUTES,
            15, // flex interval
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(REMINDER_WORK_TAG)
            .build()
        
        // Enqueue work
        workManager.enqueueUniquePeriodicWork(
            REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    private fun cancelReminder() {
        workManager.cancelUniqueWork(REMINDER_WORK_NAME)
    }
    
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            successMessage = null,
            errorMessage = null
        )
    }
    
    companion object {
        const val REMINDER_WORK_NAME = "water_reminder_work"
        const val REMINDER_WORK_TAG = "water_reminder"
    }
}
