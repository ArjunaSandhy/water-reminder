package com.waterreminder.domain.usecase

import com.waterreminder.data.repository.WaterRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AddWaterUseCaseTest {
    
    private lateinit var repository: WaterRepository
    private lateinit var useCase: AddWaterUseCase
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = AddWaterUseCase(repository)
    }
    
    @Test
    fun `should add water entry successfully with valid input`() = runTest {
        // Given
        val amountMl = 250
        val note = "After workout"
        val expectedId = 1L
        coEvery { repository.addWaterEntry(amountMl, note) } returns expectedId
        
        // When
        val result = useCase(amountMl, note)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedId, result.getOrNull())
        coVerify { repository.addWaterEntry(amountMl, note) }
    }
    
    @Test
    fun `should add water entry successfully without note`() = runTest {
        // Given
        val amountMl = 500
        val expectedId = 2L
        coEvery { repository.addWaterEntry(amountMl, null) } returns expectedId
        
        // When
        val result = useCase(amountMl, null)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedId, result.getOrNull())
        coVerify { repository.addWaterEntry(amountMl, null) }
    }
    
    @Test
    fun `should fail when amount is less than 50ml`() = runTest {
        // Given
        val amountMl = 40
        val note = "Too small"
        
        // When
        val result = useCase(amountMl, note)
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Minimal 50 ml", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.addWaterEntry(any(), any()) }
    }
    
    @Test
    fun `should fail when amount is more than 2000ml`() = runTest {
        // Given
        val amountMl = 2100
        val note = "Too large"
        
        // When
        val result = useCase(amountMl, note)
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Maksimal 2000 ml", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.addWaterEntry(any(), any()) }
    }
    
    @Test
    fun `should accept minimum valid amount of 50ml`() = runTest {
        // Given
        val amountMl = 50
        val expectedId = 3L
        coEvery { repository.addWaterEntry(amountMl, null) } returns expectedId
        
        // When
        val result = useCase(amountMl, null)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedId, result.getOrNull())
    }
    
    @Test
    fun `should accept maximum valid amount of 2000ml`() = runTest {
        // Given
        val amountMl = 2000
        val expectedId = 4L
        coEvery { repository.addWaterEntry(amountMl, null) } returns expectedId
        
        // When
        val result = useCase(amountMl, null)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedId, result.getOrNull())
    }
    
    @Test
    fun `should fail when note exceeds 200 characters`() = runTest {
        // Given
        val amountMl = 250
        val longNote = "a".repeat(201)
        
        // When
        val result = useCase(amountMl, longNote)
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Catatan maksimal 200 karakter", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.addWaterEntry(any(), any()) }
    }
    
    @Test
    fun `should accept note with exactly 200 characters`() = runTest {
        // Given
        val amountMl = 250
        val note = "a".repeat(200)
        val expectedId = 5L
        coEvery { repository.addWaterEntry(amountMl, note) } returns expectedId
        
        // When
        val result = useCase(amountMl, note)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedId, result.getOrNull())
    }
    
    @Test
    fun `should handle repository exception`() = runTest {
        // Given
        val amountMl = 250
        val note = "Test"
        val exception = RuntimeException("Database error")
        coEvery { repository.addWaterEntry(amountMl, note) } throws exception
        
        // When
        val result = useCase(amountMl, note)
        
        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
    
    @Test
    fun `should trim and accept empty note as null`() = runTest {
        // Given
        val amountMl = 300
        val emptyNote = ""
        val expectedId = 6L
        coEvery { repository.addWaterEntry(amountMl, null) } returns expectedId
        
        // When
        val result = useCase(amountMl, emptyNote)
        
        // Then
        assertTrue(result.isSuccess)
        // Note: In actual implementation, empty string should be passed as null
        // but test verifies the useCase handles it correctly
    }
    
    @Test
    fun `should handle common drink amounts`() = runTest {
        // Given
        val commonAmounts = listOf(150, 200, 250, 500)
        commonAmounts.forEach { amount ->
            coEvery { repository.addWaterEntry(amount, null) } returns amount.toLong()
        }
        
        // When & Then
        commonAmounts.forEach { amount ->
            val result = useCase(amount, null)
            assertTrue(result.isSuccess, "Amount $amount should be valid")
            assertEquals(amount.toLong(), result.getOrNull())
        }
    }
}
