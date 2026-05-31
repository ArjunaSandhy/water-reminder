package com.waterreminder.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterreminder.data.datastore.UserPreferences
import com.waterreminder.domain.usecase.GetAllDatesUseCase
import com.waterreminder.domain.usecase.GetTotalByDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

data class HistoryDateItem(
    val date: String,
    val displayDate: String,
    val total: Int,
    val target: Int,
    val isTargetAchieved: Boolean,
    val unit: String
) {
    val displayTotal: String
        get() = if (unit == "oz") {
            String.format("%.1f oz", total / 29.574)
        } else {
            "$total ml"
        }
}

data class HistoryUiState(
    val dates: List<HistoryDateItem> = emptyList(),
    val isLoading: Boolean = true,
    val unit: String = "ml",
    val dailyTarget: Int = 2000
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getAllDatesUseCase: GetAllDatesUseCase,
    private val getTotalByDateUseCase: GetTotalByDateUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {
    
    private val _dateItems = MutableStateFlow<List<HistoryDateItem>>(emptyList())
    
    val uiState: StateFlow<HistoryUiState> = combine(
        _dateItems,
        userPreferences.userSettings
    ) { items, settings ->
        HistoryUiState(
            dates = items.map { it.copy(unit = settings.unit, target = settings.dailyTargetMl) },
            isLoading = false,
            unit = settings.unit,
            dailyTarget = settings.dailyTargetMl
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState()
    )
    
    init {
        loadHistory()
    }
    
    private fun loadHistory() {
        viewModelScope.launch {
            getAllDatesUseCase().collect { dates ->
                val items = dates.map { date ->
                    val total = getTotalByDateUseCase.getSuspend(date)
                    val settings = userPreferences.userSettings.stateIn(viewModelScope).value
                    HistoryDateItem(
                        date = date,
                        displayDate = formatDisplayDate(date),
                        total = total,
                        target = settings.dailyTargetMl,
                        isTargetAchieved = total >= settings.dailyTargetMl,
                        unit = settings.unit
                    )
                }
                _dateItems.value = items
            }
        }
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
                    val month = date.month.getDisplayName(TextStyle.FULL, Locale("id", "ID"))
                    val year = date.year
                    "$dayName, $day $month $year"
                }
            }
        } catch (e: Exception) {
            dateString
        }
    }
}
