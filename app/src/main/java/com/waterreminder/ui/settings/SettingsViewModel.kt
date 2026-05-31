package com.waterreminder.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterreminder.data.repository.WaterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val targetMl: Int = 2000,
    val unit: String = "ml",
    val isSaving: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val validationError: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: WaterRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                repository.dailyTargetMl,
                repository.unit
            ) { targetMl, unit ->
                SettingsUiState(
                    targetMl = targetMl,
                    unit = unit
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
    
    fun setTarget(target: String) {
        val targetInt = target.toIntOrNull()
        val validationError = when {
            targetInt == null -> "Target harus berupa angka"
            targetInt < 500 -> "Target minimal 500 ml"
            targetInt > 5000 -> "Target maksimal 5000 ml"
            else -> null
        }
        
        _uiState.value = _uiState.value.copy(
            targetMl = targetInt ?: _uiState.value.targetMl,
            validationError = validationError
        )
    }
    
    fun setUnit(unit: String) {
        _uiState.value = _uiState.value.copy(unit = unit)
    }
    
    fun saveSettings() {
        val currentState = _uiState.value
        
        if (currentState.validationError != null) {
            return
        }
        
        _uiState.value = currentState.copy(isSaving = true, errorMessage = null)
        
        viewModelScope.launch {
            try {
                repository.setDailyTarget(currentState.targetMl)
                repository.setUnit(currentState.unit)
                
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
    
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            successMessage = null,
            errorMessage = null
        )
    }
}
