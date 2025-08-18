package com.example.crm.unit.controllers

import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.Payload
import com.example.crm.controllers.ContactController
import com.example.crm.routing.request.ContactRequest
import com.example.crm.routing.response.ContactResponse
import com.example.crm.services.ContactService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

@DisplayName("Contact Controller Tests")
class ContactControllerTest {

    private lateinit var mockContactService: ContactService
    private lateinit var contactController: ContactController
    private lateinit var mockCall: ApplicationCall
    private lateinit var mockApplicationResponse: ApplicationResponse
    private lateinit var mockParameters: Parameters
    private lateinit var mockPrincipal: JWTPrincipal
    private lateinit var mockPayload: Payload

    private val testUserId = UUID.randomUUID()
    private val otherUserId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        mockContactService = mockk()
        contactController = ContactController(mockContactService)
        mockCall = mockk()
        mockApplicationResponse = mockk()
        mockParameters = mockk()
        mockPrincipal = mockk()
        mockPayload = mockk()

        every { mockCall.response } returns mockApplicationResponse
        every { mockCall.parameters } returns mockParameters
        every { mockCall.principal<JWTPrincipal>() } returns mockPrincipal
        every { mockPrincipal.payload } returns mockPayload

        every { mockApplicationResponse.header(any<String>(), any<String>()) } just Runs
        coEvery { mockCall.respond(any<HttpStatusCode>()) } just Runs
        coEvery { mockCall.respond(any<HttpStatusCode>(), any<String>()) } just Runs
        coEvery { mockCall.respond(any<List<ContactResponse>>()) } just Runs
        coEvery { mockCall.respond(any<ContactResponse>()) } just Runs
    }

    @Test
    @DisplayName("Should get contacts for authorized user")
    fun testGetContactsByUserIdAuthorized() = runBlocking {
        // Given
        val contacts = listOf(
            ContactResponse(1, testUserId, "John Doe", "Tech Corp", "+123", "john@tech.com"),
            ContactResponse(2, testUserId, "Jane Smith", "Design Co", "+456", "jane@design.com")
        )

        every { mockParameters["userId"] } returns testUserId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")
        coEvery { mockContactService.findByUserId(testUserId) } returns contacts

        // When
        contactController.getContactsByUserId(mockCall)

        // Then
        coVerify { mockContactService.findByUserId(testUserId) }
        coVerify { mockCall.respond(contacts) }
    }

    @Test
    @DisplayName("Should return 403 when user tries to access other user's contacts")
    fun testGetContactsByUserIdUnauthorized() = runBlocking {
        // Given
        every { mockParameters["userId"] } returns otherUserId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")

        // When
        contactController.getContactsByUserId(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.Forbidden, "You can only access your own contacts") }
        coVerify(exactly = 0) { mockContactService.findByUserId(any()) }
    }

    @Test
    @DisplayName("Should allow admin to access any user's contacts")
    fun testGetContactsByUserIdAsAdmin() = runBlocking {
        // Given
        val contacts = listOf(
            ContactResponse(1, otherUserId, "John Doe", "Tech Corp", "+123", "john@tech.com")
        )

        every { mockParameters["userId"] } returns otherUserId.toString()
        setupAuthenticatedUser(testUserId.toString(), "admin")
        coEvery { mockContactService.findByUserId(otherUserId) } returns contacts

        // When
        contactController.getContactsByUserId(mockCall)

        // Then
        coVerify { mockContactService.findByUserId(otherUserId) }
        coVerify { mockCall.respond(contacts) }
    }

    @Test
    @DisplayName("Should return 400 for invalid user ID format")
    fun testGetContactsByUserIdInvalidFormat() = runBlocking {
        // Given
        every { mockParameters["userId"] } returns "invalid-uuid"

        // When
        contactController.getContactsByUserId(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.BadRequest, "Invalid user ID format") }
    }

    @Test
    @DisplayName("Should return 400 when user ID is missing")
    fun testGetContactsByUserIdMissing() = runBlocking {
        // Given
        every { mockParameters["userId"] } returns null

        // When
        contactController.getContactsByUserId(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.BadRequest, "Invalid user ID") }
    }

    @Test
    @DisplayName("Should create contact for authorized user")
    fun testCreateContactAuthorized() = runBlocking {
        // Given
        val contactRequest = ContactRequest("John Doe", "Tech Corp", "+123", "john@tech.com")
        val createdContact = ContactResponse(1, testUserId, "John Doe", "Tech Corp", "+123", "john@tech.com")

        every { mockParameters["userId"] } returns testUserId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")
        coEvery { mockCall.receive<ContactRequest>() } returns contactRequest
        coEvery { mockContactService.create(testUserId, contactRequest) } returns createdContact

        // When
        contactController.createContact(mockCall)

        // Then
        coVerify { mockContactService.create(testUserId, contactRequest) }
        coVerify { mockCall.respond(HttpStatusCode.Created, createdContact) }
    }

    @Test
    @DisplayName("Should return 403 when creating contact for other user")
    fun testCreateContactUnauthorized() = runBlocking {
        // Given
        every { mockParameters["userId"] } returns otherUserId.toString()
        setupAuthenticatedUser(testUserId.toString(), "user")

        // When
        contactController.createContact(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.Forbidden, "You can only create contacts for yourself") }
        coVerify(exactly = 0) { mockCall.receive<ContactRequest>() }
    }

    @Test
    @DisplayName("Should handle missing role claim and default to user permissions")
    fun testMissingRoleClaim() = runBlocking {
        // Given
        every { mockParameters["userId"] } returns testUserId.toString()
        setupAuthenticatedUserWithoutRole(testUserId.toString()) // No role claim
        coEvery { mockContactService.findByUserId(testUserId) } returns emptyList()

        // When
        contactController.getContactsByUserId(mockCall)

        // Then
        coVerify { mockContactService.findByUserId(testUserId) }
        coVerify { mockCall.respond(emptyList<ContactResponse>()) }
    }

    private fun setupAuthenticatedUser(userId: String, role: String) {
        val mockUserIdClaim = mockk<Claim>()
        val mockRoleClaim = mockk<Claim>()

        every { mockPayload.getClaim("userId") } returns mockUserIdClaim
        every { mockPayload.getClaim("role") } returns mockRoleClaim
        every { mockUserIdClaim.asString() } returns userId
        every { mockRoleClaim.asString() } returns role
    }

    private fun setupAuthenticatedUserWithoutRole(userId: String) {
        val mockUserIdClaim = mockk<Claim>()

        every { mockPayload.getClaim("userId") } returns mockUserIdClaim
        every { mockPayload.getClaim("role") } returns null // No role claim
        every { mockUserIdClaim.asString() } returns userId
    }
}