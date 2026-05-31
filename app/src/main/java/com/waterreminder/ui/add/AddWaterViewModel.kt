package com.waterreminder.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterreminder.domain.usecase.AddWaterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddWaterUiState(
    val amountMl: Int = 250,
    val note: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val validationError: String? = null
)

@HiltViewModel
class AddWaterViewModel @Inject constructor(
    private val addWaterUseCase: AddWaterUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddWaterUiState())
    val uiState: StateFlow<AddWaterUiState> = _uiState.asStateFlow()
    
    fun setAmount(amount: Int) {
        val validationError = when {
            amount < 50 -> "Minimal 50 ml"
            amount > 2000 -> "Maksimal 2000 ml"
            else -> null
        }
        _uiState.value = _uiState.value.copy(
            amountMl = amount,
            validationError = validationError
        )
    }
    
    fun incrementAmount() {
        val newAmount = (_uiState.value.amountMl + 50).coerceAtMost(2000)
        setAmount(newAmount)
    }
    
    fun decrementAmount() {
        val newAmount = (_uiState.value.amountMl - 50).coerceAtLeast(50)
        setAmount(newAmount)
    }
    
    fun setNote(note: String) {
        val validationError = if (note.length > 200) {
            "Catatan maksimal 200 karakter"
        } else null
        
        _uiState.value = _uiState.value.copy(
            note = note,
            validationError = validationError
        )
    }
    
    fun saveWaterEntry(onSuccess: (Int) -> Unit) {
        val currentState = _uiState.value
        
        if (currentState.validationError != null) {
            return
        }
        
        _uiState.value = currentState.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            val result = addWaterUseCase(
                amountMl = currentState.amountMl,
                note = currentState.note.ifBlank { null }
            )
            
            result.fold(
                onSuccess = {
                    _uiState.value = currentState.copy(isLoading = false)
                    onSuccess(currentState.amountMl)
                },
                onFailure = { error ->
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Gagal menyimpan data"
                    )
                }
            )
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
