package com.waterreminder.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterreminder.data.datastore.UserPreferences
import com.waterreminder.data.datastore.UserSettings
import com.waterreminder.domain.usecase.GetTodayWaterIntakeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val totalToday: Int = 0,
    val dailyTarget: Int = 2000,
    val unit: String = "ml",
    val isLoading: Boolean = true,
    val error: String? = null,
    val isTargetAchieved: Boolean = false
) {
    val progress: Float
        get() = (totalToday.toFloat() / dailyTarget.toFloat()).coerceIn(0f, 1f)
    
    val percentage: Int
        get() = (progress * 100).toInt()
    
    val displayTotal: String
        get() = if (unit == "oz") {
            String.format("%.1f", totalToday / 29.574)
        } else {
            totalToday.toString()
        }
    
    val displayTarget: String
        get() = if (unit == "oz") {
            String.format("%.1f", dailyTarget / 29.574)
        } else {
            dailyTarget.toString()
        }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTodayWaterIntakeUseCase: GetTodayWaterIntakeUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {
    
    private val _errorState = MutableStateFlow<String?>(null)
    
    val uiState: StateFlow<HomeUiState> = combine(
        getTodayWaterIntakeUseCase(),
        userPreferences.userSettings,
        _errorState
    ) { total, settings, error ->
        HomeUiState(
            totalToday = total,
            dailyTarget = settings.dailyTargetMl,
            unit = settings.unit,
            isLoading = false,
            error = error,
            isTargetAchieved = total >= settings.dailyTargetMl
        )
    }.catch { e ->
        emit(HomeUiState(
            isLoading = false,
            error = "Gagal memuat data: ${e.message}"
        ))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )
    
    fun clearError() {
        _errorState.value = null
    }
}
