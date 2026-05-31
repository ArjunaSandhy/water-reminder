package com.waterreminder.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterreminder.BuildConfig
import com.waterreminder.data.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val dailyTargetMl: Int = 2000,
    val unit: String = "ml",
    val appVersion: String = BuildConfig.VERSION_NAME
) {
    val displayTarget: String
        get() = if (unit == "oz") {
            String.format("%.1f oz", dailyTargetMl / 29.574)
        } else {
            "$dailyTargetMl ml"
        }
    
    val unitOptions: List<String> = listOf("ml", "oz")
    
    fun getUnitDisplayName(unit: String): String {
        return when (unit) {
            "ml" -> "Mililiter (ml)"
            "oz" -> "Ounce (oz)"
            else -> unit
        }
    }
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {
    
    val uiState: StateFlow<SettingsUiState> = userPreferences.userSettings
        .map { settings ->
            SettingsUiState(
                dailyTargetMl = settings.dailyTargetMl,
                unit = settings.unit
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsUiState()
        )
    
    fun setDailyTarget(targetMl: Int) {
        viewModelScope.launch {
            userPreferences.setDailyTarget(targetMl.coerceIn(500, 5000))
        }
    }
    
    fun setUnit(unit: String) {
        viewModelScope.launch {
            userPreferences.setUnit(unit)
        }
    }
}
