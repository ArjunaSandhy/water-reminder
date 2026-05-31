package com.waterreminder.domain.usecase

import com.waterreminder.data.repository.WaterRepository
import javax.inject.Inject

class AddWaterUseCase @Inject constructor(
    private val repository: WaterRepository
) {
    suspend operator fun invoke(amountMl: Int, note: String?): Result<Long> {
        return try {
            // Validasi input
            if (amountMl < 50) {
                return Result.failure(IllegalArgumentException("Minimal 50 ml"))
            }
            if (amountMl > 2000) {
                return Result.failure(IllegalArgumentException("Maksimal 2000 ml"))
            }
            if (note != null && note.length > 200) {
                return Result.failure(IllegalArgumentException("Catatan maksimal 200 karakter"))
            }
            
            val entryId = repository.addWaterEntry(amountMl, note)
            Result.success(entryId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
