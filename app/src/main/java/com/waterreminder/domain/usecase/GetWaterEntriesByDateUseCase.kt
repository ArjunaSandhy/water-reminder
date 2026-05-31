package com.waterreminder.domain.usecase

import com.waterreminder.data.local.entity.WaterEntry
import com.waterreminder.data.repository.WaterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWaterEntriesByDateUseCase @Inject constructor(
    private val repository: WaterRepository
) {
    operator fun invoke(date: String): Flow<List<WaterEntry>> {
        return repository.getEntriesByDate(date)
    }
}
