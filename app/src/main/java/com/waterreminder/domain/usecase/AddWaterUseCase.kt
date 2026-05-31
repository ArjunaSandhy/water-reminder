package com.waterreminder.domain.usecase

import com.waterreminder.data.repository.WaterRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class AddWaterUseCase @Inject constructor(
    private val repository: WaterRepository
) {
    sealed class Result {
        data class Success(val entryId: Long, val totalToday: Int) : Result()
        data class Error(val message: String) : Result()
    }
    
    suspend operator fun invoke(amountMl: Int, note: String? = null): Result {
        return try {
            // Validate amount
            if (amountMl < 50) {
                return Result.Error("Jumlah minimal adalah 50 ml")
            }
            if (amountMl > 2000) {
                return Result.Error("Jumlah maksimal adalah 2000 ml")
            }
            
            // Validate note
            val validatedNote = note?.takeIf { it.isNotBlank() }?.take(200)
            
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val entryId = repository.addWaterEntry(amountMl, validatedNote, today)
            val totalToday = repository.getTotalByDateSuspend(today)
            
            Result.Success(entryId, totalToday)
        } catch (e: Exception) {
            Result.Error("Gagal menyimpan data: ${e.message}")
        }
    }
}
