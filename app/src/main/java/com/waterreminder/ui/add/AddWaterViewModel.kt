package com.waterreminder.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterreminder.data.datastore.UserPreferences
import com.waterreminder.domain.usecase.AddWaterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddWaterUiState(
    val amount: Int = 200,
    val note: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val savedAmount: Int? = null, // Non-null when save is successful
    val unit: String = "ml"
) {
    val isAmountValid: Boolean
        get() = amount in 50..2000
    
    val canSave: Boolean
        get() = isAmountValid && !isLoading
    
    val validationError: String?
        get() = when {
            amount < 50 -> "Jumlah minimal adalah 50 ml"
            amount > 2000 -> "Jumlah maksimal adalah 2000 ml"
            else -> null
        }
}

@HiltViewModel
class AddWaterViewModel @Inject constructor(
    private val addWaterUseCase: AddWaterUseCase,
    userPreferences: UserPreferences
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddWaterUiState())
    val uiState: StateFlow<AddWaterUiState> = _uiState.asStateFlow()
    
    val unit: StateFlow<String> = userPreferences.userSettings
        .map { it.unit }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "ml"
        )
    
    init {
        viewModelScope.launch {
            unit.collect { newUnit ->
                _uiState.update { it.copy(unit = newUnit) }
            }
        }
    }
    
    fun setAmount(amount: Int) {
        _uiState.update { it.copy(amount = amount.coerceIn(50, 2000)) }
    }
    
    fun setNote(note: String) {
        _uiState.update { it.copy(note = note.take(200)) }
    }
    
    fun selectQuickAmount(amount: Int) {
        _uiState.update { it.copy(amount = amount) }
    }
    
    fun saveWaterEntry() {
        if (!_uiState.value.canSave) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = addWaterUseCase(
                amountMl = _uiState.value.amount,
                note = _uiState.value.note.takeIf { it.isNotBlank() }
            )) {
                is AddWaterUseCase.Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            savedAmount = _uiState.value.amount
                        )
                    }
                }
                is AddWaterUseCase.Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun resetSavedState() {
        _uiState.update { it.copy(savedAmount = null) }
    }
}
