package com.waterreminder.ui.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterreminder.data.datastore.UserPreferences
import com.waterreminder.data.local.entity.WaterEntry
import com.waterreminder.domain.usecase.DeleteWaterEntryUseCase
import com.waterreminder.domain.usecase.GetTotalByDateUseCase
import com.waterreminder.domain.usecase.GetWaterEntriesByDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

data class HistoryDetailUiState(
    val date: String = "",
    val displayDate: String = "",
    val entries: List<WaterEntry> = emptyList(),
    val total: Int = 0,
    val target: Int = 2000,
    val unit: String = "ml",
    val isLoading: Boolean = true,
    val isToday: Boolean = false,
    val canGoNext: Boolean = false,
    val canGoPrevious: Boolean = true,
    val error: String? = null,
    val deleteSuccess: Boolean = false
) {
    val displayTotal: String
        get() = if (unit == "oz") {
            String.format("%.1f oz", total / 29.574)
        } else {
            "$total ml"
        }
    
    val isTargetAchieved: Boolean
        get() = total >= target
}

@HiltViewModel
class HistoryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getWaterEntriesByDateUseCase: GetWaterEntriesByDateUseCase,
    private val getTotalByDateUseCase: GetTotalByDateUseCase,
    private val deleteWaterEntryUseCase: DeleteWaterEntryUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {
    
    private val initialDate: String = savedStateHandle.get<String>("date") 
        ?: LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    
    private val _currentDate = MutableStateFlow(initialDate)
    private val _deleteSuccess = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    
    val uiState: StateFlow<HistoryDetailUiState> = combine(
        _currentDate,
        userPreferences.userSettings,
        _deleteSuccess,
        _error
    ) { date, settings, deleteSuccess, error ->
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val isToday = date == today
        
        HistoryDetailUiState(
            date = date,
            displayDate = formatDisplayDate(date),
            target = settings.dailyTargetMl,
            unit = settings.unit,
            isLoading = false,
            isToday = isToday,
            canGoNext = !isToday,
            deleteSuccess = deleteSuccess,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryDetailUiState(date = initialDate)
    )
    
    private val _entries = MutableStateFlow<List<WaterEntry>>(emptyList())
    val entries: StateFlow<List<WaterEntry>> = _entries
    
    private val _total = MutableStateFlow(0)
    val total: StateFlow<Int> = _total
    
    init {
        loadEntriesForDate(initialDate)
    }
    
    private fun loadEntriesForDate(date: String) {
        viewModelScope.launch {
            getWaterEntriesByDateUseCase(date).collect { entries ->
                _entries.value = entries
            }
        }
        viewModelScope.launch {
            getTotalByDateUseCase(date).collect { total ->
                _total.value = total
            }
        }
    }
    
    fun goToNextDay() {
        val currentDate = LocalDate.parse(_currentDate.value, DateTimeFormatter.ISO_LOCAL_DATE)
        val today = LocalDate.now()
        
        if (currentDate.isBefore(today)) {
            val nextDate = currentDate.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
            _currentDate.value = nextDate
            loadEntriesForDate(nextDate)
        }
    }
    
    fun goToPreviousDay() {
        val currentDate = LocalDate.parse(_currentDate.value, DateTimeFormatter.ISO_LOCAL_DATE)
        val previousDate = currentDate.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
        _currentDate.value = previousDate
        loadEntriesForDate(previousDate)
    }
    
    fun deleteEntry(id: Long) {
        viewModelScope.launch {
            when (val result = deleteWaterEntryUseCase(id)) {
                is DeleteWaterEntryUseCase.Result.Success -> {
                    _deleteSuccess.value = true
                }
                is DeleteWaterEntryUseCase.Result.Error -> {
                    _error.value = result.message
                }
            }
        }
    }
    
    fun clearDeleteSuccess() {
        _deleteSuccess.value = false
    }
    
    fun clearError() {
        _error.value = null
    }
    
    private fun formatDisplayDate(dateString: String): String {
        return try {
            val date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)
            
            when (date) {
                today -> "Hari Ini"
                yesterday -> "Kemarin"
                else -> {
                    val dayName = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("id", "ID"))
                    val day = date.dayOfMonth
                    val month = date.month.getDisplayName(TextStyle.SHORT, Locale("id", "ID"))
                    val year = date.year
                    "$dayName, $day $month $year"
                }
            }
        } catch (e: Exception) {
            dateString
        }
    }
}
