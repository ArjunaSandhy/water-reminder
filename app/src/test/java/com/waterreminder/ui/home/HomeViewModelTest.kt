package com.waterreminder.ui.home

import app.cash.turbine.test
import com.waterreminder.data.datastore.UserPreferences
import com.waterreminder.data.datastore.UserSettings
import com.waterreminder.domain.usecase.GetTodayWaterIntakeUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var getTodayWaterIntakeUseCase: GetTodayWaterIntakeUseCase
    private lateinit var userPreferences: UserPreferences
    private lateinit var viewModel: HomeViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getTodayWaterIntakeUseCase = mockk()
        userPreferences = mockk()
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state should have loading true`() = runTest {
        // Given
        every { getTodayWaterIntakeUseCase() } returns flowOf(0)
        every { userPreferences.userSettings } returns flowOf(UserSettings())
        
        // When
        viewModel = HomeViewModel(getTodayWaterIntakeUseCase, userPreferences)
        
        // Then
        val initialState = viewModel.uiState.value
        assertTrue(initialState.isLoading)
    }
    
    @Test
    fun `uiState should reflect total water intake from use case`() = runTest {
        // Given
        val expectedTotal = 1500
        every { getTodayWaterIntakeUseCase() } returns flowOf(expectedTotal)
        every { userPreferences.userSettings } returns flowOf(UserSettings(dailyTargetMl = 2000))
        
        // When
        viewModel = HomeViewModel(getTodayWaterIntakeUseCase, userPreferences)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(expectedTotal, state.totalToday)
            assertEquals(2000, state.dailyTarget)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `isTargetAchieved should be true when total equals or exceeds target`() = runTest {
        // Given
        every { getTodayWaterIntakeUseCase() } returns flowOf(2000)
        every { userPreferences.userSettings } returns flowOf(UserSettings(dailyTargetMl = 2000))
        
        // When
        viewModel = HomeViewModel(getTodayWaterIntakeUseCase, userPreferences)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isTargetAchieved)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `isTargetAchieved should be false when total is less than target`() = runTest {
        // Given
        every { getTodayWaterIntakeUseCase() } returns flowOf(1500)
        every { userPreferences.userSettings } returns flowOf(UserSettings(dailyTargetMl = 2000))
        
        // When
        viewModel = HomeViewModel(getTodayWaterIntakeUseCase, userPreferences)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isTargetAchieved)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `progress should be calculated correctly`() = runTest {
        // Given
        every { getTodayWaterIntakeUseCase() } returns flowOf(1000)
        every { userPreferences.userSettings } returns flowOf(UserSettings(dailyTargetMl = 2000))
        
        // When
        viewModel = HomeViewModel(getTodayWaterIntakeUseCase, userPreferences)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(0.5f, state.progress, 0.01f)
            assertEquals(50, state.percentage)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `progress should be capped at 1f when exceeding target`() = runTest {
        // Given
        every { getTodayWaterIntakeUseCase() } returns flowOf(3000)
        every { userPreferences.userSettings } returns flowOf(UserSettings(dailyTargetMl = 2000))
        
        // When
        viewModel = HomeViewModel(getTodayWaterIntakeUseCase, userPreferences)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1f, state.progress, 0.01f)
            assertEquals(100, state.percentage)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `unit should be reflected from user settings`() = runTest {
        // Given
        every { getTodayWaterIntakeUseCase() } returns flowOf(1000)
        every { userPreferences.userSettings } returns flowOf(UserSettings(unit = "oz"))
        
        // When
        viewModel = HomeViewModel(getTodayWaterIntakeUseCase, userPreferences)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("oz", state.unit)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `displayTotal should convert to oz when unit is oz`() = runTest {
        // Given
        every { getTodayWaterIntakeUseCase() } returns flowOf(296) // approximately 10 oz
        every { userPreferences.userSettings } returns flowOf(UserSettings(unit = "oz"))
        
        // When
        viewModel = HomeViewModel(getTodayWaterIntakeUseCase, userPreferences)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.displayTotal.contains("10"))
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `clearError should reset error state`() = runTest {
        // Given
        every { getTodayWaterIntakeUseCase() } returns flowOf(0)
        every { userPreferences.userSettings } returns flowOf(UserSettings())
        
        viewModel = HomeViewModel(getTodayWaterIntakeUseCase, userPreferences)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.clearError()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(null, state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
