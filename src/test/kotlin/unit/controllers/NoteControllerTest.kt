package com.example.crm.unit.controllers

import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.Payload
import com.example.crm.controllers.NoteController
import com.example.crm.routing.request.NoteRequest
import com.example.crm.routing.response.NoteResponse
import com.example.crm.services.NoteService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

@DisplayName("Note Controller Tests")
class NoteControllerTest {

    private lateinit var mockNoteService: NoteService
    private lateinit var noteController: NoteController
    private lateinit var mockCall: ApplicationCall
    private lateinit var mockApplicationResponse: ApplicationResponse
    private lateinit var mockParameters: Parameters
    private lateinit var mockPrincipal: JWTPrincipal
    private lateinit var mockPayload: Payload

    private val testUserId = UUID.randomUUID()
    private val otherUserId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        mockNoteService = mockk()
        noteController = NoteController(mockNoteService)
        mockCall = mockk()
        mockApplicationResponse = mockk()
        mockParameters = mockk()
        mockPrincipal = mockk()
        mockPayload = mockk()

        every { mockCall.response } returns mockApplicationResponse
        every { mockCall.parameters } returns mockParameters
        every { mockCall.principal<JWTPrincipal>() } returns mockPrincipal
        every { mockPrincipal.payload } returns mockPayload

        coEvery { mockCall.respond(any<HttpStatusCode>()) } just Runs
        coEvery { mockCall.respond(any<HttpStatusCode>(), any<String>()) } just Runs
        coEvery { mockCall.respond(any<List<NoteResponse>>()) } just Runs
        coEvery { mockCall.respond(any<NoteResponse>()) } just Runs
    }

    @Test
    @DisplayName("Should get notes for authorized user")
    fun testGetNotesByUserIdAuthorized() = runBlocking {
        // Given
        val notes = listOf(
            NoteResponse(1, testUserId, listOf(1), "Meeting Notes", "Discussed project"),
            NoteResponse(2, testUserId, listOf(2), "Follow-up", "Send proposal")
        )

        every { mockParameters["userId"] } returns testUserId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")
        coEvery { mockNoteService.findByUserId(testUserId) } returns notes

        // When
        noteController.getNotesByUserId(mockCall)

        // Then
        coVerify { mockNoteService.findByUserId(testUserId) }
        coVerify { mockCall.respond(HttpStatusCode.OK, notes) }
    }

    @Test
    @DisplayName("Should return 403 when accessing other user's notes")
    fun testGetNotesByUserIdUnauthorized() = runBlocking {
        // Given
        every { mockParameters["userId"] } returns otherUserId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")

        // When
        noteController.getNotesByUserId(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.Forbidden, "You can only access your own notes") }
        coVerify(exactly = 0) { mockNoteService.findByUserId(any()) }
    }

    @Test
    @DisplayName("Should allow admin to access any user's notes")
    fun testGetNotesByUserIdAsAdmin() = runBlocking {
        // Given
        val notes = listOf(
            NoteResponse(1, otherUserId, listOf(1), "Admin Access", "Admin can see this")
        )

        every { mockParameters["userId"] } returns otherUserId.toString()
        setupAuthenticatedUser(testUserId.toString(), "admin")
        coEvery { mockNoteService.findByUserId(otherUserId) } returns notes

        // When
        noteController.getNotesByUserId(mockCall)

        // Then
        coVerify { mockNoteService.findByUserId(otherUserId) }
        coVerify { mockCall.respond(HttpStatusCode.OK, notes) }
    }

    @Test
    @DisplayName("Should create note for authorized user")
    fun testCreateNoteAuthorized() = runBlocking {
        // Given
        val noteRequest = NoteRequest(title = "New Note", description = "Note description", contactIds = listOf(1, 2))
        val createdNote = NoteResponse(1, testUserId, listOf(1, 2), "New Note", "Note description")

        every { mockParameters["userId"] } returns testUserId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")
        coEvery { mockCall.receive<NoteRequest>() } returns noteRequest
        coEvery { mockNoteService.create(testUserId, noteRequest) } returns createdNote

        // When
        noteController.createNote(mockCall)

        // Then
        coVerify { mockNoteService.create(testUserId, noteRequest) }
        coVerify { mockCall.respond(HttpStatusCode.Created, createdNote) }
    }

    @Test
    @DisplayName("Should return 403 when creating note for other user")
    fun testCreateNoteUnauthorized() = runBlocking {
        // Given
        every { mockParameters["userId"] } returns otherUserId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")

        // When
        noteController.createNote(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.Forbidden, "You can only create notes for yourself") }
        coVerify(exactly = 0) { mockCall.receive<NoteRequest>() }
    }

    @Test
    @DisplayName("Should get specific note when authorized")
    fun testGetNoteByIdAuthorized() = runBlocking {
        // Given
        val noteId = 1
        val note = NoteResponse(noteId, testUserId, listOf(1), "Test Note", "Test description")

        every { mockParameters["userId"] } returns testUserId.toString()
        every { mockParameters["noteId"] } returns noteId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")
        coEvery { mockNoteService.findById(noteId, testUserId) } returns note

        // When
        noteController.getNoteById(mockCall)

        // Then
        coVerify { mockNoteService.findById(noteId, testUserId) }
        coVerify { mockCall.respond(note) }
    }

    @Test
    @DisplayName("Should return 404 when note not found")
    fun testGetNoteByIdNotFound() = runBlocking {
        // Given
        val noteId = 999

        every { mockParameters["userId"] } returns testUserId.toString()
        every { mockParameters["noteId"] } returns noteId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")
        coEvery { mockNoteService.findById(noteId, testUserId) } returns null

        // When
        noteController.getNoteById(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.NotFound, "Note not found") }
    }

    @Test
    @DisplayName("Should update note when authorized")
    fun testUpdateNoteAuthorized() = runBlocking {
        // Given
        val noteId = 1
        val noteRequest = NoteRequest(title = "Updated Note", description = "Updated description", contactIds = listOf(1))

        every { mockParameters["userId"] } returns testUserId.toString()
        every { mockParameters["noteId"] } returns noteId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")
        coEvery { mockCall.receive<NoteRequest>() } returns noteRequest
        coEvery { mockNoteService.update(noteId, testUserId, noteRequest) } returns true

        // When
        noteController.updateNote(mockCall)

        // Then
        coVerify { mockNoteService.update(noteId, testUserId, noteRequest) }
        coVerify { mockCall.respond(HttpStatusCode.OK, "Note updated successfully") }
    }

    @Test
    @DisplayName("Should delete note when authorized")
    fun testDeleteNoteAuthorized() = runBlocking {
        // Given
        val noteId = 1

        every { mockParameters["userId"] } returns testUserId.toString()
        every { mockParameters["noteId"] } returns noteId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")
        coEvery { mockNoteService.delete(noteId, testUserId) } returns true

        // When
        noteController.deleteNote(mockCall)

        // Then
        coVerify { mockNoteService.delete(noteId, testUserId) }
        coVerify { mockCall.respond(HttpStatusCode.OK, "Note deleted successfully") }
    }

    @Test
    @DisplayName("Should get notes by contact ID when authorized")
    fun testGetNotesByContactIdAuthorized() = runBlocking {
        // Given
        val contactId = 1
        val notes = listOf(
            NoteResponse(1, testUserId, listOf(contactId), "Contact Note", "About this contact")
        )

        every { mockParameters["userId"] } returns testUserId.toString()
        every { mockParameters["contactId"] } returns contactId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")
        coEvery { mockNoteService.findByContact(testUserId, contactId) } returns notes

        // When
        noteController.getNotesByContactId(mockCall)

        // Then
        coVerify { mockNoteService.findByContact(testUserId, contactId) }
        coVerify { mockCall.respond(HttpStatusCode.OK, notes) }
    }

    @Test
    @DisplayName("Should return 400 for invalid note ID format")
    fun testGetNoteByIdInvalidFormat() = runBlocking {
        // Given
        every { mockParameters["userId"] } returns testUserId.toString()
        every { mockParameters["noteId"] } returns "invalid-id"

        // When
        noteController.getNoteById(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.BadRequest, "Invalid note ID") }
    }

    @Test
    @DisplayName("Should return 400 for invalid user ID format")
    fun testGetNotesByUserIdInvalidFormat() = runBlocking {
        // Given
        every { mockParameters["userId"] } returns "invalid-uuid"

        // When
        noteController.getNotesByUserId(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.BadRequest, "Invalid user ID format") }
    }

    @Test
    @DisplayName("Should handle malformed note request")
    fun testCreateNoteMalformedRequest() = runBlocking {
        // Given
        every { mockParameters["userId"] } returns testUserId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")
        coEvery { mockCall.receive<NoteRequest>() } throws RuntimeException("JSON parsing failed")

        // When
        noteController.createNote(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.BadRequest, "Invalid request format") }
    }

    @Test
    @DisplayName("Should handle service exception during note operations")
    fun testNoteOperationServiceException() = runBlocking {
        // Given
        val noteId = 1

        every { mockParameters["userId"] } returns testUserId.toString()
        every { mockParameters["noteId"] } returns noteId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")
        coEvery { mockNoteService.findById(noteId, testUserId) } throws RuntimeException("Database error")

        // When
        noteController.getNoteById(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.InternalServerError, "Failed to fetch note") }
    }

    @Test
    @DisplayName("Should return empty list for contact with no notes")
    fun testGetNotesByContactIdEmptyList() = runBlocking {
        // Given
        val contactId = 1

        every { mockParameters["userId"] } returns testUserId.toString()
        every { mockParameters["contactId"] } returns contactId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")
        coEvery { mockNoteService.findByContact(testUserId, contactId) } returns emptyList()

        // When
        noteController.getNotesByContactId(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.OK, emptyList<NoteResponse>()) }
    }

    @Test
    @DisplayName("Should handle note creation with empty contact IDs")
    fun testCreateNoteEmptyContactIds() = runBlocking {
        // Given
        val noteRequest = NoteRequest(title = "General Note", description = "No specific contact", contactIds = emptyList())
        val createdNote = NoteResponse(1, testUserId, emptyList(), "General Note", "No specific contact")

        every { mockParameters["userId"] } returns testUserId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")
        coEvery { mockCall.receive<NoteRequest>() } returns noteRequest
        coEvery { mockNoteService.create(testUserId, noteRequest) } returns createdNote

        // When
        noteController.createNote(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.Created, createdNote) }
    }

    @Test
    @DisplayName("Should handle note creation with null description")
    fun testCreateNoteNullDescription() = runBlocking {
        // Given
        val noteRequest = NoteRequest(title = "Title Only", description = null, contactIds = listOf(1))
        val createdNote = NoteResponse(1, testUserId, listOf(1), "Title Only", null)

        every { mockParameters["userId"] } returns testUserId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")
        coEvery { mockCall.receive<NoteRequest>() } returns noteRequest
        coEvery { mockNoteService.create(testUserId, noteRequest) } returns createdNote

        // When
        noteController.createNote(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.Created, createdNote) }
        assertEquals(null, createdNote.description)
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent note")
    fun testUpdateNoteNotFound() = runBlocking {
        // Given
        val noteId = 999
        val noteRequest = NoteRequest(title = "Updated Note", description = "Updated description", contactIds = listOf(1))

        every { mockParameters["userId"] } returns testUserId.toString()
        every { mockParameters["noteId"] } returns noteId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")
        coEvery { mockCall.receive<NoteRequest>() } returns noteRequest
        coEvery { mockNoteService.update(noteId, testUserId, noteRequest) } returns false

        // When
        noteController.updateNote(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.NotFound, "Note not found") }
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent note")
    fun testDeleteNoteNotFound() = runBlocking {
        // Given
        val noteId = 999

        every { mockParameters["userId"] } returns testUserId.toString()
        every { mockParameters["noteId"] } returns noteId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")
        coEvery { mockNoteService.delete(noteId, testUserId) } returns false

        // When
        noteController.deleteNote(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.NotFound, "Note not found") }
    }

    @Test
    @DisplayName("Should return 403 when updating other user's note")
    fun testUpdateNoteUnauthorized() = runBlocking {
        // Given
        every { mockParameters["userId"] } returns otherUserId.toString()
        every { mockParameters["noteId"] } returns "1"
        setupAuthenticatedUser(testUserId.toString(), "user")

        // When
        noteController.updateNote(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.Forbidden, "You can only update your own notes") }
        coVerify(exactly = 0) { mockCall.receive<NoteRequest>() }
    }

    @Test
    @DisplayName("Should return 403 when deleting other user's note")
    fun testDeleteNoteUnauthorized() = runBlocking {
        // Given
        every { mockParameters["userId"] } returns otherUserId.toString()
        every { mockParameters["noteId"] } returns "1"
        setupAuthenticatedUser(testUserId.toString(), "user")

        // When
        noteController.deleteNote(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.Forbidden, "You can only delete your own notes") }
        coVerify(exactly = 0) { mockNoteService.delete(any(), any()) }
    }

    @Test
    @DisplayName("Should return 400 for missing note ID")
    fun testGetNoteByIdMissingNoteId() = runBlocking {
        // Given
        every { mockParameters["userId"] } returns testUserId.toString()
        every { mockParameters["noteId"] } returns null

        // When
        noteController.getNoteById(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.BadRequest, "Invalid note ID") }
    }

    @Test
    @DisplayName("Should return 400 for missing contact ID")
    fun testGetNotesByContactIdMissingContactId() = runBlocking {
        // Given
        every { mockParameters["userId"] } returns testUserId.toString()
        every { mockParameters["contactId"] } returns null

        // When
        noteController.getNotesByContactId(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.BadRequest, "Invalid contact ID") }
    }

    @Test
    @DisplayName("Should handle service exception during note creation")
    fun testCreateNoteServiceException() = runBlocking {
        // Given
        val noteRequest = NoteRequest(title = "Test Note", description = "Description", contactIds = listOf(1))

        every { mockParameters["userId"] } returns testUserId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")
        coEvery { mockCall.receive<NoteRequest>() } returns noteRequest
        coEvery { mockNoteService.create(testUserId, noteRequest) } throws RuntimeException("Database error")

        // When
        noteController.createNote(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.BadRequest, "Invalid request format") }
    }

    @Test
    @DisplayName("Should handle invalid user ID format in all operations")
    fun testInvalidUserIdFormat() = runBlocking {
        // Given
        every { mockParameters["userId"] } returns "invalid-uuid"

        // When & Then for different operations
        noteController.getNotesByUserId(mockCall)
        coVerify { mockCall.respond(HttpStatusCode.BadRequest, "Invalid user ID format") }

        clearMocks(mockCall)
        every { mockCall.parameters } returns mockParameters
        coEvery { mockCall.respond(any<HttpStatusCode>(), any<String>()) } just Runs

        noteController.createNote(mockCall)
        coVerify { mockCall.respond(HttpStatusCode.BadRequest, "Invalid user ID format") }
    }

    private fun setupAuthenticatedUser(userId: String, role: String) {
        val mockUserIdClaim = mockk<Claim>()
        val mockRoleClaim = mockk<Claim>()

        every { mockPayload.getClaim("userId") } returns mockUserIdClaim
        every { mockPayload.getClaim("role") } returns mockRoleClaim
        every { mockUserIdClaim.asString() } returns userId
        every { mockRoleClaim.asString() } returns role
    }
}