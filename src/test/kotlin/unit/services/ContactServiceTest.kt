package com.example.crm.unit.services

import com.example.crm.models.Contact
import com.example.crm.routing.request.ContactRequest
import com.example.crm.services.ContactService
import com.example.crm.services.repositories.ContactRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

@DisplayName("Contact Service Tests")
class ContactServiceTest {

    private lateinit var mockContactRepository: ContactRepository
    private lateinit var contactService: ContactService

    private val testUserId = UUID.randomUUID()
    private val otherUserId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        mockContactRepository = mockk()
        contactService = ContactService(mockContactRepository)
    }

    @Test
    @DisplayName("Should find contacts by user ID")
    fun testFindByUserId() = runBlocking {
        // Given
        val contacts = listOf(
            Contact(1, testUserId, "John Doe", "Tech Corp", "+123", "john@tech.com"),
            Contact(2, testUserId, "Jane Smith", "Design Co", "+456", "jane@design.com")
        )
        coEvery { mockContactRepository.findByUserId(testUserId) } returns contacts

        // When
        val result = contactService.findByUserId(testUserId)

        // Then
        assertEquals(2, result.size, "Should return all user's contacts")
        assertEquals("John Doe", result[0].name)
        assertEquals("Jane Smith", result[1].name)
        assertTrue(result.all { it.userId == testUserId })
    }

    @Test
    @DisplayName("Should return empty list when user has no contacts")
    fun testFindByUserIdEmpty() = runBlocking {
        // Given
        coEvery { mockContactRepository.findByUserId(testUserId) } returns emptyList()

        // When
        val result = contactService.findByUserId(testUserId)

        // Then
        assertTrue(result.isEmpty(), "Should return empty list for user with no contacts")
    }

    @Test
    @DisplayName("Should find contact by ID when user owns it")
    fun testFindByIdAuthorized() = runBlocking {
        // Given
        val contact = Contact(1, testUserId, "John Doe", "Tech Corp", "+123", "john@tech.com")
        coEvery { mockContactRepository.findById(1) } returns contact

        // When
        val result = contactService.findById(1, testUserId)

        // Then
        assertNotNull(result, "Should return contact when user owns it")
        assertEquals("John Doe", result!!.name)
        assertEquals(testUserId, result.userId)
    }

    @Test
    @DisplayName("Should return null when user doesn't own contact")
    fun testFindByIdUnauthorized() = runBlocking {
        // Given
        val contact = Contact(1, otherUserId, "John Doe", "Tech Corp", "+123", "john@tech.com")
        coEvery { mockContactRepository.findById(1) } returns contact

        // When
        val result = contactService.findById(1, testUserId)

        // Then
        assertNull(result, "Should return null when user doesn't own contact")
    }

    @Test
    @DisplayName("Should return null when contact doesn't exist")
    fun testFindByIdNotFound() = runBlocking {
        // Given
        coEvery { mockContactRepository.findById(999) } returns null

        // When
        val result = contactService.findById(999, testUserId)

        // Then
        assertNull(result, "Should return null when contact doesn't exist")
    }

    @Test
    @DisplayName("Should create contact for user")
    fun testCreateContact() = runBlocking {
        // Given
        val request = ContactRequest("John Doe", "Tech Corp", "+123456789", "john@tech.com")
        val createdContact = Contact(1, testUserId, "John Doe", "Tech Corp", "+123456789", "john@tech.com")

        coEvery {
            mockContactRepository.create(testUserId, "John Doe", "Tech Corp", "+123456789", "john@tech.com")
        } returns createdContact

        // When
        val result = contactService.create(testUserId, request)

        // Then
        assertEquals("John Doe", result.name)
        assertEquals("Tech Corp", result.company)
        assertEquals("+123456789", result.phoneNumber)
        assertEquals("john@tech.com", result.contactEmail)
        assertEquals(testUserId, result.userId)
    }

    @Test
    @DisplayName("Should create contact with minimal data")
    fun testCreateContactMinimalData() = runBlocking {
        // Given
        val request = ContactRequest("Jane Doe", null, null, null)
        val createdContact = Contact(2, testUserId, "Jane Doe", null, null, null)

        coEvery {
            mockContactRepository.create(testUserId, "Jane Doe", null, null, null)
        } returns createdContact

        // When
        val result = contactService.create(testUserId, request)

        // Then
        assertEquals("Jane Doe", result.name)
        assertNull(result.company)
        assertNull(result.phoneNumber)
        assertNull(result.contactEmail)
        assertEquals(testUserId, result.userId)
    }

    @Test
    @DisplayName("Should update contact when user owns it")
    fun testUpdateContactAuthorized() = runBlocking {
        // Given
        val existingContact = Contact(1, testUserId, "John Doe", "Tech Corp", "+123", "john@tech.com")
        val request = ContactRequest("John Updated", "New Corp", "+999", "john@new.com")

        coEvery { mockContactRepository.findById(1) } returns existingContact
        coEvery {
            mockContactRepository.update(1, "John Updated", "New Corp", "+999", "john@new.com")
        } returns true

        // When
        val result = contactService.update(1, testUserId, request)

        // Then
        assertTrue(result, "Should successfully update contact")

        coVerify {
            mockContactRepository.update(1, "John Updated", "New Corp", "+999", "john@new.com")
        }
    }

    @Test
    @DisplayName("Should not update contact when user doesn't own it")
    fun testUpdateContactUnauthorized() = runBlocking {
        // Given
        val existingContact = Contact(1, otherUserId, "John Doe", "Tech Corp", "+123", "john@tech.com")
        val request = ContactRequest("John Updated", "New Corp", "+999", "john@new.com")

        coEvery { mockContactRepository.findById(1) } returns existingContact

        // When
        val result = contactService.update(1, testUserId, request)

        // Then
        assertFalse(result, "Should not update contact user doesn't own")

        // Verify update was never called
        coVerify(exactly = 0) { mockContactRepository.update(any(), any(), any(), any(), any()) }
    }

    @Test
    @DisplayName("Should return false when updating non-existent contact")
    fun testUpdateNonExistentContact() = runBlocking {
        // Given
        val request = ContactRequest("John Updated", "New Corp", "+999", "john@new.com")
        coEvery { mockContactRepository.findById(999) } returns null

        // When
        val result = contactService.update(999, testUserId, request)

        // Then
        assertFalse(result, "Should return false for non-existent contact")
    }

    @Test
    @DisplayName("Should delete contact when user owns it")
    fun testDeleteContactAuthorized() = runBlocking {
        // Given
        val existingContact = Contact(1, testUserId, "John Doe", "Tech Corp", "+123", "john@tech.com")

        coEvery { mockContactRepository.findById(1) } returns existingContact
        coEvery { mockContactRepository.delete(1) } returns true

        // When
        val result = contactService.delete(1, testUserId)

        // Then
        assertTrue(result, "Should successfully delete contact")

        coVerify { mockContactRepository.delete(1) }
    }

    @Test
    @DisplayName("Should not delete contact when user doesn't own it")
    fun testDeleteContactUnauthorized() = runBlocking {
        // Given
        val existingContact = Contact(1, otherUserId, "John Doe", "Tech Corp", "+123", "john@tech.com")

        coEvery { mockContactRepository.findById(1) } returns existingContact

        // When
        val result = contactService.delete(1, testUserId)

        // Then
        assertFalse(result, "Should not delete contact user doesn't own")

        // Verify delete was never called
        coVerify(exactly = 0) { mockContactRepository.delete(any()) }
    }

    @Test
    @DisplayName("Should return false when deleting non-existent contact")
    fun testDeleteNonExistentContact() = runBlocking {
        // Given
        coEvery { mockContactRepository.findById(999) } returns null

        // When
        val result = contactService.delete(999, testUserId)

        // Then
        assertFalse(result, "Should return false for non-existent contact")
    }

    @Test
    @DisplayName("Should handle repository failure during update")
    fun testUpdateRepositoryFailure() = runBlocking {
        // Given
        val existingContact = Contact(1, testUserId, "John Doe", "Tech Corp", "+123", "john@tech.com")
        val request = ContactRequest("John Updated", "New Corp", "+999", "john@new.com")

        coEvery { mockContactRepository.findById(1) } returns existingContact
        coEvery {
            mockContactRepository.update(1, "John Updated", "New Corp", "+999", "john@new.com")
        } returns false

        // When
        val result = contactService.update(1, testUserId, request)

        // Then
        assertFalse(result, "Should return false when repository update fails")
    }

    @Test
    @DisplayName("Should handle repository failure during delete")
    fun testDeleteRepositoryFailure() = runBlocking {
        // Given
        val existingContact = Contact(1, testUserId, "John Doe", "Tech Corp", "+123", "john@tech.com")

        coEvery { mockContactRepository.findById(1) } returns existingContact
        coEvery { mockContactRepository.delete(1) } returns false

        // When
        val result = contactService.delete(1, testUserId)

        // Then
        assertFalse(result, "Should return false when repository delete fails")
    }
}