package com.waterreminder.ui.success

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterreminder.data.datastore.UserPreferences
import com.waterreminder.domain.usecase.GetTodayWaterIntakeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SuccessUiState(
    val amountAdded: Int = 0,
    val totalToday: Int = 0,
    val dailyTarget: Int = 2000,
    val unit: String = "ml",
    val isTargetAchieved: Boolean = false
) {
    val displayAmountAdded: String
        get() = if (unit == "oz") {
            String.format("%.1f oz", amountAdded / 29.574)
        } else {
            "$amountAdded ml"
        }
    
    val displayTotalToday: String
        get() = if (unit == "oz") {
            String.format("%.1f oz", totalToday / 29.574)
        } else {
            "$totalToday ml"
        }
}

@HiltViewModel
class SuccessViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getTodayWaterIntakeUseCase: GetTodayWaterIntakeUseCase,
    userPreferences: UserPreferences
) : ViewModel() {
    
    private val amountAdded: Int = savedStateHandle.get<Int>("amountAdded") ?: 0
    
    val uiState: StateFlow<SuccessUiState> = combine(
        getTodayWaterIntakeUseCase(),
        userPreferences.userSettings
    ) { total, settings ->
        SuccessUiState(
            amountAdded = amountAdded,
            totalToday = total,
            dailyTarget = settings.dailyTargetMl,
            unit = settings.unit,
            isTargetAchieved = total >= settings.dailyTargetMl
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SuccessUiState(amountAdded = amountAdded)
    )
}
