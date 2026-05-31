package com.waterreminder.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.waterreminder.domain.usecase.DailyProgress
import com.waterreminder.domain.usecase.GetDailyProgressUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var getDailyProgressUseCase: GetDailyProgressUseCase
    private lateinit var viewModel: HomeViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getDailyProgressUseCase = mockk()
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state should be loading`() = runTest {
        // Given
        every { getDailyProgressUseCase() } returns flowOf()
        
        // When
        viewModel = HomeViewModel(getDailyProgressUseCase)
        
        // Then
        assertTrue(viewModel.uiState.value.isLoading)
    }
    
    @Test
    fun `should update state when progress is loaded successfully`() = runTest {
        // Given
        val progress = DailyProgress(
            currentMl = 1000,
            targetMl = 2000,
            percentage = 0.5f,
            remainingMl = 1000,
            isTargetReached = false
        )
        every { getDailyProgressUseCase() } returns flowOf(progress)
        
        // When
        viewModel = HomeViewModel(getDailyProgressUseCase)
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(1000, state.currentMl)
            assertEquals(2000, state.targetMl)
            assertEquals(0.5f, state.percentage)
            assertEquals(1000, state.remainingMl)
            assertFalse(state.isTargetReached)
            assertEquals(null, state.errorMessage)
        }
    }
    
    @Test
    fun `should show target reached when progress is 100 percent`() = runTest {
        // Given
        val progress = DailyProgress(
            currentMl = 2000,
            targetMl = 2000,
            percentage = 1.0f,
            remainingMl = 0,
            isTargetReached = true
        )
        every { getDailyProgressUseCase() } returns flowOf(progress)
        
        // When
        viewModel = HomeViewModel(getDailyProgressUseCase)
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isTargetReached)
            assertEquals(0, state.remainingMl)
            assertEquals(1.0f, state.percentage)
        }
    }
    
    @Test
    fun `should handle empty state with zero consumption`() = runTest {
        // Given
        val progress = DailyProgress(
            currentMl = 0,
            targetMl = 2000,
            percentage = 0f,
            remainingMl = 2000,
            isTargetReached = false
        )
        every { getDailyProgressUseCase() } returns flowOf(progress)
        
        // When
        viewModel = HomeViewModel(getDailyProgressUseCase)
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(0, state.currentMl)
            assertEquals(2000, state.remainingMl)
            assertEquals(0f, state.percentage)
            assertFalse(state.isTargetReached)
        }
    }
    
    @Test
    fun `should handle over target consumption`() = runTest {
        // Given
        val progress = DailyProgress(
            currentMl = 2500,
            targetMl = 2000,
            percentage = 1.0f, // coerced to 1.0
            remainingMl = 0,
            isTargetReached = true
        )
        every { getDailyProgressUseCase() } returns flowOf(progress)
        
        // When
        viewModel = HomeViewModel(getDailyProgressUseCase)
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(2500, state.currentMl)
            assertEquals(1.0f, state.percentage) // Should be capped at 1.0
            assertTrue(state.isTargetReached)
        }
    }
    
    @Test
    fun `retry should reload progress`() = runTest {
        // Given
        val progress = DailyProgress(
            currentMl = 500,
            targetMl = 2000,
            percentage = 0.25f,
            remainingMl = 1500,
            isTargetReached = false
        )
        every { getDailyProgressUseCase() } returns flowOf(progress)
        viewModel = HomeViewModel(getDailyProgressUseCase)
        
        // When
        viewModel.retry()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(500, state.currentMl)
        }
    }
    
    @Test
    fun `clearError should remove error message`() = runTest {
        // Given
        every { getDailyProgressUseCase() } returns flowOf(
            DailyProgress(1000, 2000, 0.5f, 1000, false)
        )
        viewModel = HomeViewModel(getDailyProgressUseCase)
        
        // When
        viewModel.clearError()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(null, state.errorMessage)
        }
    }
}
