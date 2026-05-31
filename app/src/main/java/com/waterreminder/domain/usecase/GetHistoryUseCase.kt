package com.waterreminder.domain.usecase

import com.waterreminder.data.local.WaterEntry
import com.waterreminder.data.repository.WaterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class DailyHistory(
    val date: String,
    val formattedDate: String,
    val entries: List<WaterEntry>,
    val totalMl: Int,
    val targetMl: Int,
    val isTargetReached: Boolean
)

class GetHistoryUseCase @Inject constructor(
    private val repository: WaterRepository
) {
    operator fun invoke(): Flow<List<DailyHistory>> {
        return combine(
            repository.getAllDates(),
            repository.dailyTargetMl
        ) { dates, targetMl ->
            dates.map { date ->
                val entries = repository.getEntriesByDate(date)
                // Note: We'll need to collect this in the ViewModel
                // For now, return placeholder
                DailyHistory(
                    date = date,
                    formattedDate = repository.formatDate(date),
                    entries = emptyList(), // Will be populated in ViewModel
                    totalMl = 0,
                    targetMl = targetMl,
                    isTargetReached = false
                )
            }
        }
    }
    
    fun getEntriesByDate(date: String): Flow<List<WaterEntry>> {
        return repository.getEntriesByDate(date)
    }
    
    fun getTotalByDate(date: String): Flow<Int?> {
        return repository.getTotalByDate(date)
    }
}
