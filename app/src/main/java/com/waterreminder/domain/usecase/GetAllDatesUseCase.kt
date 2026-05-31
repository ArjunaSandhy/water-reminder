package com.waterreminder.domain.usecase

import com.waterreminder.data.repository.WaterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllDatesUseCase @Inject constructor(
    private val repository: WaterRepository
) {
    operator fun invoke(): Flow<List<String>> {
        return repository.getAllDates()
    }
}
