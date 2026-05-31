package com.waterreminder.domain.usecase

import com.waterreminder.data.repository.WaterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTotalByDateUseCase @Inject constructor(
    private val repository: WaterRepository
) {
    operator fun invoke(date: String): Flow<Int> {
        return repository.getTotalByDate(date).map { it ?: 0 }
    }
    
    suspend fun getSuspend(date: String): Int {
        return repository.getTotalByDateSuspend(date)
    }
}
