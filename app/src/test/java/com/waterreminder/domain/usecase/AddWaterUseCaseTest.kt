package com.waterreminder.domain.usecase

import com.waterreminder.data.repository.WaterRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddWaterUseCaseTest {
    
    private lateinit var waterRepository: WaterRepository
    private lateinit var addWaterUseCase: AddWaterUseCase
    
    @Before
    fun setup() {
        waterRepository = mockk()
        addWaterUseCase = AddWaterUseCase(waterRepository)
    }
    
    @Test
    fun `invoke with valid amount should return Success`() = runTest {
        // Given
        val amount = 200
        val expectedEntryId = 1L
        val expectedTotal = 500
        
        coEvery { waterRepository.addWaterEntry(amount, null, any()) } returns expectedEntryId
        coEvery { waterRepository.getTotalByDateSuspend(any()) } returns expectedTotal
        
        // When
        val result = addWaterUseCase(amount)
        
        // Then
        assertTrue(result is AddWaterUseCase.Result.Success)
        val successResult = result as AddWaterUseCase.Result.Success
        assertEquals(expectedEntryId, successResult.entryId)
        assertEquals(expectedTotal, successResult.totalToday)
    }
    
    @Test
    fun `invoke with amount less than 50 should return Error`() = runTest {
        // Given
        val amount = 30
        
        // When
        val result = addWaterUseCase(amount)
        
        // Then
        assertTrue(result is AddWaterUseCase.Result.Error)
        val errorResult = result as AddWaterUseCase.Result.Error
        assertEquals("Jumlah minimal adalah 50 ml", errorResult.message)
    }
    
    @Test
    fun `invoke with amount greater than 2000 should return Error`() = runTest {
        // Given
        val amount = 2500
        
        // When
        val result = addWaterUseCase(amount)
        
        // Then
        assertTrue(result is AddWaterUseCase.Result.Error)
        val errorResult = result as AddWaterUseCase.Result.Error
        assertEquals("Jumlah maksimal adalah 2000 ml", errorResult.message)
    }
    
    @Test
    fun `invoke with minimum valid amount 50 should return Success`() = runTest {
        // Given
        val amount = 50
        
        coEvery { waterRepository.addWaterEntry(amount, null, any()) } returns 1L
        coEvery { waterRepository.getTotalByDateSuspend(any()) } returns amount
        
        // When
        val result = addWaterUseCase(amount)
        
        // Then
        assertTrue(result is AddWaterUseCase.Result.Success)
    }
    
    @Test
    fun `invoke with maximum valid amount 2000 should return Success`() = runTest {
        // Given
        val amount = 2000
        
        coEvery { waterRepository.addWaterEntry(amount, null, any()) } returns 1L
        coEvery { waterRepository.getTotalByDateSuspend(any()) } returns amount
        
        // When
        val result = addWaterUseCase(amount)
        
        // Then
        assertTrue(result is AddWaterUseCase.Result.Success)
    }
    
    @Test
    fun `invoke with note should pass note to repository`() = runTest {
        // Given
        val amount = 200
        val note = "After workout"
        
        coEvery { waterRepository.addWaterEntry(amount, note, any()) } returns 1L
        coEvery { waterRepository.getTotalByDateSuspend(any()) } returns amount
        
        // When
        addWaterUseCase(amount, note)
        
        // Then
        coVerify { waterRepository.addWaterEntry(amount, note, any()) }
    }
    
    @Test
    fun `invoke with blank note should pass null to repository`() = runTest {
        // Given
        val amount = 200
        val note = "   "
        
        coEvery { waterRepository.addWaterEntry(amount, null, any()) } returns 1L
        coEvery { waterRepository.getTotalByDateSuspend(any()) } returns amount
        
        // When
        addWaterUseCase(amount, note)
        
        // Then
        coVerify { waterRepository.addWaterEntry(amount, null, any()) }
    }
    
    @Test
    fun `invoke with long note should truncate to 200 characters`() = runTest {
        // Given
        val amount = 200
        val longNote = "a".repeat(300)
        val truncatedNote = "a".repeat(200)
        
        coEvery { waterRepository.addWaterEntry(amount, truncatedNote, any()) } returns 1L
        coEvery { waterRepository.getTotalByDateSuspend(any()) } returns amount
        
        // When
        addWaterUseCase(amount, longNote)
        
        // Then
        coVerify { waterRepository.addWaterEntry(amount, truncatedNote, any()) }
    }
    
    @Test
    fun `invoke when repository throws exception should return Error`() = runTest {
        // Given
        val amount = 200
        val errorMessage = "Database error"
        
        coEvery { waterRepository.addWaterEntry(any(), any(), any()) } throws Exception(errorMessage)
        
        // When
        val result = addWaterUseCase(amount)
        
        // Then
        assertTrue(result is AddWaterUseCase.Result.Error)
        val errorResult = result as AddWaterUseCase.Result.Error
        assertTrue(errorResult.message.contains(errorMessage))
    }
    
    @Test
    fun `invoke should use today's date`() = runTest {
        // Given
        val amount = 200
        
        coEvery { waterRepository.addWaterEntry(amount, null, any()) } returns 1L
        coEvery { waterRepository.getTotalByDateSuspend(any()) } returns amount
        
        // When
        addWaterUseCase(amount)
        
        // Then
        coVerify { 
            waterRepository.addWaterEntry(
                amount, 
                null, 
                match { it.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) }
            ) 
        }
    }
    
    @Test
    fun `invoke should return correct total from repository`() = runTest {
        // Given
        val amount = 200
        val expectedTotal = 1500
        
        coEvery { waterRepository.addWaterEntry(amount, null, any()) } returns 1L
        coEvery { waterRepository.getTotalByDateSuspend(any()) } returns expectedTotal
        
        // When
        val result = addWaterUseCase(amount)
        
        // Then
        assertTrue(result is AddWaterUseCase.Result.Success)
        assertEquals(expectedTotal, (result as AddWaterUseCase.Result.Success).totalToday)
    }
    
    @Test
    fun `invoke with exactly 49 ml should return Error`() = runTest {
        // Given
        val amount = 49
        
        // When
        val result = addWaterUseCase(amount)
        
        // Then
        assertTrue(result is AddWaterUseCase.Result.Error)
    }
    
    @Test
    fun `invoke with exactly 2001 ml should return Error`() = runTest {
        // Given
        val amount = 2001
        
        // When
        val result = addWaterUseCase(amount)
        
        // Then
        assertTrue(result is AddWaterUseCase.Result.Error)
    }
}
