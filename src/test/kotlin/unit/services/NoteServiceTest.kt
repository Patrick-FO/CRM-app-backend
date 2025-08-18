package com.example.crm.unit.services

import com.example.crm.models.Contact
import com.example.crm.models.Note
import com.example.crm.routing.request.NoteRequest
import com.example.crm.services.NoteService
import com.example.crm.services.repositories.ContactRepository
import com.example.crm.services.repositories.NoteRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

@DisplayName("Note Service Tests")
class NoteServiceTest {

    private lateinit var mockNoteRepository: NoteRepository
    private lateinit var mockContactRepository: ContactRepository
    private lateinit var noteService: NoteService

    private val testUserId = UUID.randomUUID()
    private val otherUserId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        mockNoteRepository = mockk()
        mockContactRepository = mockk()
        noteService = NoteService(mockNoteRepository, mockContactRepository)
    }

    @Test
    @DisplayName("Should find notes by user ID")
    fun testFindByUserId() = runBlocking {
        // Given
        val notes = listOf(
            Note(1, testUserId, listOf(1, 2), "Meeting Notes", "Discussed project"),
            Note(2, testUserId, listOf(1), "Follow-up", "Send proposal")
        )
        coEvery { mockNoteRepository.findByUserId(testUserId) } returns notes

        // When
        val result = noteService.findByUserId(testUserId)

        // Then
        assertEquals(2, result.size, "Should return all user's notes")
        assertEquals("Meeting Notes", result[0].title)
        assertEquals("Follow-up", result[1].title)
        assertTrue(result.all { it.userId == testUserId })
    }

    @Test
    @DisplayName("Should return empty list when user has no notes")
    fun testFindByUserIdEmpty() = runBlocking {
        // Given
        coEvery { mockNoteRepository.findByUserId(testUserId) } returns emptyList()

        // When
        val result = noteService.findByUserId(testUserId)

        // Then
        assertTrue(result.isEmpty(), "Should return empty list for user with no notes")
    }

    @Test
    @DisplayName("Should find notes by contact when user owns contact")
    fun testFindByContactAuthorized() = runBlocking {
        // Given
        val contactId = 1
        val contact = Contact(contactId, testUserId, "John Doe", "Tech Corp", "+123", "john@tech.com")
        val notes = listOf(
            Note(1, testUserId, listOf(contactId), "Meeting with John", "Great discussion")
        )

        coEvery { mockContactRepository.findById(contactId) } returns contact
        coEvery { mockNoteRepository.findByContact(testUserId, contactId) } returns notes

        // When
        val result = noteService.findByContact(testUserId, contactId)

        // Then
        assertEquals(1, result.size, "Should return notes for the contact")
        assertEquals("Meeting with John", result[0].title)
        assertTrue(result[0].contactIds.contains(contactId))
    }

    @Test
    @DisplayName("Should return empty list when user doesn't own contact")
    fun testFindByContactUnauthorized() = runBlocking {
        // Given
        val contactId = 1
        val contact = Contact(contactId, otherUserId, "John Doe", "Tech Corp", "+123", "john@tech.com")

        coEvery { mockContactRepository.findById(contactId) } returns contact

        // When
        val result = noteService.findByContact(testUserId, contactId)

        // Then
        assertTrue(result.isEmpty(), "Should return empty list when user doesn't own contact")

        // Verify findByContact was never called
        coVerify(exactly = 0) { mockNoteRepository.findByContact(any(), any()) }
    }

    @Test
    @DisplayName("Should return empty list when contact doesn't exist")
    fun testFindByContactNotFound() = runBlocking {
        // Given
        val contactId = 999
        coEvery { mockContactRepository.findById(contactId) } returns null

        // When
        val result = noteService.findByContact(testUserId, contactId)

        // Then
        assertTrue(result.isEmpty(), "Should return empty list when contact doesn't exist")
    }

    @Test
    @DisplayName("Should find note by ID when user owns it")
    fun testFindByIdAuthorized() = runBlocking {
        // Given
        val note = Note(1, testUserId, listOf(1), "Test Note", "Test description")
        coEvery { mockNoteRepository.findById(1) } returns note

        // When
        val result = noteService.findById(1, testUserId)

        // Then
        assertNotNull(result, "Should return note when user owns it")
        assertEquals("Test Note", result!!.title)
        assertEquals(testUserId, result.userId)
    }

    @Test
    @DisplayName("Should return null when user doesn't own note")
    fun testFindByIdUnauthorized() = runBlocking {
        // Given
        val note = Note(1, otherUserId, listOf(1), "Test Note", "Test description")
        coEvery { mockNoteRepository.findById(1) } returns note

        // When
        val result = noteService.findById(1, testUserId)

        // Then
        assertNull(result, "Should return null when user doesn't own note")
    }

    @Test
    @DisplayName("Should return null when note doesn't exist")
    fun testFindByIdNotFound() = runBlocking {
        // Given
        coEvery { mockNoteRepository.findById(999) } returns null

        // When
        val result = noteService.findById(999, testUserId)

        // Then
        assertNull(result, "Should return null when note doesn't exist")
    }

    @Test
    @DisplayName("Should create note with valid contact IDs")
    fun testCreateNoteWithValidContacts() = runBlocking {
        // Given
        val contact1 = Contact(1, testUserId, "John", "Corp1", "+123", "john@corp1.com")
        val contact2 = Contact(2, testUserId, "Jane", "Corp2", "+456", "jane@corp2.com")
        val request = NoteRequest(title = "Meeting Notes", description = "Great meeting", contactIds = listOf(1, 2))
        val createdNote = Note(1, testUserId, listOf(1, 2), "Meeting Notes", "Great meeting")

        coEvery { mockContactRepository.findById(1) } returns contact1
        coEvery { mockContactRepository.findById(2) } returns contact2
        coEvery {
            mockNoteRepository.create(testUserId, "Meeting Notes", "Great meeting", listOf(1, 2))
        } returns createdNote

        // When
        val result = noteService.create(testUserId, request)

        // Then
        assertEquals("Meeting Notes", result.title)
        assertEquals("Great meeting", result.description)
        assertEquals(listOf(1, 2), result.contactIds)
        assertEquals(testUserId, result.userId)
    }

    @Test
    @DisplayName("Should filter out invalid contact IDs when creating note")
    fun testCreateNoteFiltersInvalidContacts() = runBlocking {
        // Given
        val validContact = Contact(1, testUserId, "John", "Corp1", "+123", "john@corp1.com")
        val invalidContact = Contact(2, otherUserId, "Jane", "Corp2", "+456", "jane@corp2.com")
        val request = NoteRequest(title = "Meeting Notes", description = "Great meeting", contactIds = listOf(1, 2, 999))
        val createdNote = Note(1, testUserId, listOf(1), "Meeting Notes", "Great meeting")

        coEvery { mockContactRepository.findById(1) } returns validContact
        coEvery { mockContactRepository.findById(2) } returns invalidContact  // Different user
        coEvery { mockContactRepository.findById(999) } returns null  // Doesn't exist
        coEvery {
            mockNoteRepository.create(testUserId, "Meeting Notes", "Great meeting", listOf(1))
        } returns createdNote

        // When
        val result = noteService.create(testUserId, request)

        // Then
        assertEquals("Meeting Notes", result.title)
        assertEquals(listOf(1), result.contactIds, "Should only include valid contact IDs")

        coVerify {
            mockNoteRepository.create(testUserId, "Meeting Notes", "Great meeting", listOf(1))
        }
    }

    @Test
    @DisplayName("Should create note with empty contact IDs list")
    fun testCreateNoteWithEmptyContactIds() = runBlocking {
        // Given
        val request = NoteRequest(title = "General Note", description = "No specific contact", contactIds = emptyList())
        val createdNote = Note(1, testUserId, emptyList(), "General Note", "No specific contact")

        coEvery {
            mockNoteRepository.create(testUserId, "General Note", "No specific contact", emptyList())
        } returns createdNote

        // When
        val result = noteService.create(testUserId, request)

        // Then
        assertEquals("General Note", result.title)
        assertTrue(result.contactIds.isEmpty(), "Should handle empty contact IDs")
    }

    @Test
    @DisplayName("Should update note when user owns it")
    fun testUpdateNoteAuthorized() = runBlocking {
        // Given
        val existingNote = Note(1, testUserId, listOf(1), "Original Title", "Original description")
        val request = NoteRequest(title = "Updated Title", description = "Updated description", contactIds = listOf(1, 2))

        coEvery { mockNoteRepository.findById(1) } returns existingNote
        coEvery {
            mockNoteRepository.update(1, "Updated Title", "Updated description", listOf(1, 2))
        } returns true

        // When
        val result = noteService.update(1, testUserId, request)

        // Then
        assertTrue(result, "Should successfully update note")

        coVerify {
            mockNoteRepository.update(1, "Updated Title", "Updated description", listOf(1, 2))
        }
    }

    @Test
    @DisplayName("Should not update note when user doesn't own it")
    fun testUpdateNoteUnauthorized() = runBlocking {
        // Given
        val existingNote = Note(1, otherUserId, listOf(1), "Original Title", "Original description")
        val request = NoteRequest(title = "Updated Title", description = "Updated description", contactIds = listOf(1, 2))

        coEvery { mockNoteRepository.findById(1) } returns existingNote

        // When
        val result = noteService.update(1, testUserId, request)

        // Then
        assertFalse(result, "Should not update note user doesn't own")

        // Verify update was never called
        coVerify(exactly = 0) { mockNoteRepository.update(any(), any(), any(), any()) }
    }

    @Test
    @DisplayName("Should return false when updating non-existent note")
    fun testUpdateNonExistentNote() = runBlocking {
        // Given
        val request = NoteRequest(title = "Updated Title", description =  "Updated description", contactIds = listOf(1))
        coEvery { mockNoteRepository.findById(999) } returns null

        // When
        val result = noteService.update(999, testUserId, request)

        // Then
        assertFalse(result, "Should return false for non-existent note")
    }

    @Test
    @DisplayName("Should delete note when user owns it")
    fun testDeleteNoteAuthorized() = runBlocking {
        // Given
        val existingNote = Note(1, testUserId, listOf(1), "Test Note", "Test description")

        coEvery { mockNoteRepository.findById(1) } returns existingNote
        coEvery { mockNoteRepository.delete(1) } returns true

        // When
        val result = noteService.delete(1, testUserId)

        // Then
        assertTrue(result, "Should successfully delete note")

        coVerify { mockNoteRepository.delete(1) }
    }

    @Test
    @DisplayName("Should not delete note when user doesn't own it")
    fun testDeleteNoteUnauthorized() = runBlocking {
        // Given
        val existingNote = Note(1, otherUserId, listOf(1), "Test Note", "Test description")

        coEvery { mockNoteRepository.findById(1) } returns existingNote

        // When
        val result = noteService.delete(1, testUserId)

        // Then
        assertFalse(result, "Should not delete note user doesn't own")

        // Verify delete was never called
        coVerify(exactly = 0) { mockNoteRepository.delete(any()) }
    }

    @Test
    @DisplayName("Should return false when deleting non-existent note")
    fun testDeleteNonExistentNote() = runBlocking {
        // Given
        coEvery { mockNoteRepository.findById(999) } returns null

        // When
        val result = noteService.delete(999, testUserId)

        // Then
        assertFalse(result, "Should return false for non-existent note")
    }

    @Test
    @DisplayName("Should handle repository failure during update")
    fun testUpdateRepositoryFailure() = runBlocking {
        // Given
        val existingNote = Note(1, testUserId, listOf(1), "Original Title", "Original description")
        val request = NoteRequest(title = "Updated Title", description = "Updated description", contactIds = listOf(1, 2))

        coEvery { mockNoteRepository.findById(1) } returns existingNote
        coEvery {
            mockNoteRepository.update(1, "Updated Title", "Updated description", listOf(1, 2))
        } returns false

        // When
        val result = noteService.update(1, testUserId, request)

        // Then
        assertFalse(result, "Should return false when repository update fails")
    }

    @Test
    @DisplayName("Should handle repository failure during delete")
    fun testDeleteRepositoryFailure() = runBlocking {
        // Given
        val existingNote = Note(1, testUserId, listOf(1), "Test Note", "Test description")

        coEvery { mockNoteRepository.findById(1) } returns existingNote
        coEvery { mockNoteRepository.delete(1) } returns false

        // When
        val result = noteService.delete(1, testUserId)

        // Then
        assertFalse(result, "Should return false when repository delete fails")
    }

    @Test
    @DisplayName("Should create note with null description")
    fun testCreateNoteWithNullDescription() = runBlocking {
        // Given
        val request = NoteRequest(title = "Title Only", description = null, contactIds = listOf(1))
        val contact = Contact(1, testUserId, "John", "Corp", "+123", "john@corp.com")
        val createdNote = Note(1, testUserId, listOf(1), "Title Only", null)

        coEvery { mockContactRepository.findById(1) } returns contact
        coEvery {
            mockNoteRepository.create(testUserId, "Title Only", null, listOf(1))
        } returns createdNote

        // When
        val result = noteService.create(testUserId, request)

        // Then
        assertEquals("Title Only", result.title)
        assertNull(result.description, "Should handle null description")
        assertEquals(listOf(1), result.contactIds)
    }

    @Test
    @DisplayName("Should preserve contact ID order when filtering")
    fun testCreateNotePreservesContactOrder() = runBlocking {
        // Given
        val contact1 = Contact(1, testUserId, "John", "Corp1", "+123", "john@corp1.com")
        val contact3 = Contact(3, testUserId, "Bob", "Corp3", "+789", "bob@corp3.com")
        val invalidContact = Contact(2, otherUserId, "Jane", "Corp2", "+456", "jane@corp2.com")

        val request = NoteRequest(title = "Test Note", description = "Description", contactIds = listOf(3, 1, 2)) // Mixed order with invalid
        val createdNote = Note(1, testUserId, listOf(3, 1), "Test Note", "Description")

        coEvery { mockContactRepository.findById(1) } returns contact1
        coEvery { mockContactRepository.findById(2) } returns invalidContact
        coEvery { mockContactRepository.findById(3) } returns contact3
        coEvery {
            mockNoteRepository.create(testUserId, "Test Note", "Description", listOf(3, 1))
        } returns createdNote

        // When
        val result = noteService.create(testUserId, request)

        // Then
        assertEquals(listOf(3, 1), result.contactIds, "Should preserve order of valid contact IDs")
    }
}