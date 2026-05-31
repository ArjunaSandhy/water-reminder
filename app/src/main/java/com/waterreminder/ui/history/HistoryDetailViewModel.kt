package com.waterreminder.ui.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterreminder.data.local.WaterEntry
import com.waterreminder.data.repository.WaterRepository
import com.waterreminder.domain.usecase.DeleteWaterEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class HistoryDetailUiState(
    val date: String = "",
    val formattedDate: String = "",
    val entries: List<WaterEntry> = emptyList(),
    val totalMl: Int = 0,
    val targetMl: Int = 2000,
    val percentage: Float = 0f,
    val isTargetReached: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val canNavigateNext: Boolean = true
)

@HiltViewModel
class HistoryDetailViewModel @Inject constructor(
    private val repository: WaterRepository,
    private val deleteWaterEntryUseCase: DeleteWaterEntryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val dateArg: String = savedStateHandle.get<String>("date") ?: getCurrentDate()
    
    private val _uiState = MutableStateFlow(HistoryDetailUiState())
    val uiState: StateFlow<HistoryDetailUiState> = _uiState.asStateFlow()
    
    init {
        loadDetailForDate(dateArg)
    }
    
    fun loadDetailForDate(date: String) {
        viewModelScope.launch {
            try {
                combine(
                    repository.getEntriesByDate(date),
                    repository.dailyTargetMl
                ) { entries, targetMl ->
                    val totalMl = entries.sumOf { it.amountMl }
                    val percentage = (totalMl.toFloat() / targetMl.toFloat()).coerceIn(0f, 1f)
                    val isToday = date == getCurrentDate()
                    
                    HistoryDetailUiState(
                        date = date,
                        formattedDate = repository.formatDate(date),
                        entries = entries,
                        totalMl = totalMl,
                        targetMl = targetMl,
                        percentage = percentage,
                        isTargetReached = totalMl >= targetMl,
                        isLoading = false,
                        errorMessage = null,
                        canNavigateNext = !isToday
                    )
                }
                    .catch { e ->
                        _uiState.value = HistoryDetailUiState(
                            date = date,
                            isLoading = false,
                            errorMessage = "Gagal memuat data: ${e.message}"
                        )
                    }
                    .collect { state ->
                        _uiState.value = state
                    }
            } catch (e: Exception) {
                _uiState.value = HistoryDetailUiState(
                    date = date,
                    isLoading = false,
                    errorMessage = "Gagal memuat data: ${e.message}"
                )
            }
        }
    }
    
    fun navigateToPreviousDay() {
        val currentDate = _uiState.value.date
        val previousDate = getPreviousDate(currentDate)
        loadDetailForDate(previousDate)
    }
    
    fun navigateToNextDay() {
        val currentDate = _uiState.value.date
        val nextDate = getNextDate(currentDate)
        if (nextDate <= getCurrentDate()) {
            loadDetailForDate(nextDate)
        }
    }
    
    fun deleteEntry(entryId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            deleteWaterEntryUseCase(entryId).fold(
                onSuccess = {
                    onSuccess()
                    // Reload current date to refresh the list
                    loadDetailForDate(_uiState.value.date)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Gagal menghapus: ${error.message}"
                    )
                }
            )
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
    
    private fun getPreviousDate(dateString: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateString) ?: Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        return dateFormat.format(calendar.time)
    }
    
    private fun getNextDate(dateString: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateString) ?: Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        return dateFormat.format(calendar.time)
    }
}
