package com.waterreminder.domain.usecase

import com.waterreminder.data.repository.WaterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetTodayWaterIntakeUseCase @Inject constructor(
    private val repository: WaterRepository
) {
    operator fun invoke(): Flow<Int> {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        return repository.getTotalByDate(today).map { it ?: 0 }
    }
}
