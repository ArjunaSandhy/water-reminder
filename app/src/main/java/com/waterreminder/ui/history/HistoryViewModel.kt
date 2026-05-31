package com.waterreminder.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterreminder.data.local.WaterEntry
import com.waterreminder.data.repository.WaterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DateSummary(
    val date: String,
    val formattedDate: String,
    val totalMl: Int,
    val targetMl: Int,
    val entries: List<WaterEntry>,
    val isTargetReached: Boolean
)

data class HistoryUiState(
    val dateSummaries: List<DateSummary> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: WaterRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
    
    init {
        loadHistory()
    }
    
    private fun loadHistory() {
        viewModelScope.launch {
            try {
                combine(
                    repository.getAllDates(),
                    repository.dailyTargetMl
                ) { dates, targetMl ->
                    dates to targetMl
                }
                    .catch { e ->
                        _uiState.value = HistoryUiState(
                            isLoading = false,
                            errorMessage = "Gagal memuat riwayat: ${e.message}"
                        )
                    }
                    .collect { (dates, targetMl) ->
                        val summaries = dates.map { date ->
                            val entries = repository.getEntriesByDate(date).first()
                            val totalMl = entries.sumOf { it.amountMl }
                            
                            DateSummary(
                                date = date,
                                formattedDate = repository.formatDate(date),
                                totalMl = totalMl,
                                targetMl = targetMl,
                                entries = entries,
                                isTargetReached = totalMl >= targetMl
                            )
                        }
                        
                        _uiState.value = HistoryUiState(
                            dateSummaries = summaries,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = HistoryUiState(
                    isLoading = false,
                    errorMessage = "Gagal memuat riwayat: ${e.message}"
                )
            }
        }
    }
    
    fun retry() {
        _uiState.value = HistoryUiState(isLoading = true)
        loadHistory()
    }
}
