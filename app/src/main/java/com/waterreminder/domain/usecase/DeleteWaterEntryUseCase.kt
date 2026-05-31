package com.waterreminder.domain.usecase

import com.waterreminder.data.repository.WaterRepository
import javax.inject.Inject

class DeleteWaterEntryUseCase @Inject constructor(
    private val repository: WaterRepository
) {
    sealed class Result {
        object Success : Result()
        data class Error(val message: String) : Result()
    }
    
    suspend operator fun invoke(id: Long): Result {
        return try {
            repository.deleteEntry(id)
            Result.Success
        } catch (e: Exception) {
            Result.Error("Gagal menghapus data: ${e.message}")
        }
    }
}
