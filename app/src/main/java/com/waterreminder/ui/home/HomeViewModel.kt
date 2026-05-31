package com.waterreminder.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterreminder.domain.usecase.GetDailyProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val currentMl: Int = 0,
    val targetMl: Int = 2000,
    val percentage: Float = 0f,
    val remainingMl: Int = 2000,
    val isTargetReached: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDailyProgressUseCase: GetDailyProgressUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadDailyProgress()
    }
    
    private fun loadDailyProgress() {
        viewModelScope.launch {
            getDailyProgressUseCase()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat data: ${e.message}"
                    )
                }
                .collect { progress ->
                    _uiState.value = HomeUiState(
                        currentMl = progress.currentMl,
                        targetMl = progress.targetMl,
                        percentage = progress.percentage,
                        remainingMl = progress.remainingMl,
                        isTargetReached = progress.isTargetReached,
                        isLoading = false,
                        errorMessage = null
                    )
                }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun retry() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        loadDailyProgress()
    }
}
