package com.waterreminder.domain.usecase

import com.waterreminder.data.repository.WaterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class DailyProgress(
    val currentMl: Int,
    val targetMl: Int,
    val percentage: Float,
    val remainingMl: Int,
    val isTargetReached: Boolean
)

class GetDailyProgressUseCase @Inject constructor(
    private val repository: WaterRepository
) {
    operator fun invoke(): Flow<DailyProgress> {
        return combine(
            repository.getTodayTotal(),
            repository.dailyTargetMl
        ) { totalMl, targetMl ->
            val current = totalMl ?: 0
            val percentage = (current.toFloat() / targetMl.toFloat()).coerceIn(0f, 1f)
            val remaining = (targetMl - current).coerceAtLeast(0)
            val isReached = current >= targetMl
            
            DailyProgress(
                currentMl = current,
                targetMl = targetMl,
                percentage = percentage,
                remainingMl = remaining,
                isTargetReached = isReached
            )
        }
    }
}
