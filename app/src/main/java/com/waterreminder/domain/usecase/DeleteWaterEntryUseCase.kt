package com.waterreminder.domain.usecase

import com.waterreminder.data.repository.WaterRepository
import javax.inject.Inject

class DeleteWaterEntryUseCase @Inject constructor(
    private val repository: WaterRepository
) {
    suspend operator fun invoke(entryId: Long): Result<Unit> {
        return try {
            repository.deleteEntry(entryId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
