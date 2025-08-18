package com.example.crm.unit.controllers

import com.example.crm.controllers.AuthController
import com.example.crm.models.User
import com.example.crm.routing.request.LoginRequest
import com.example.crm.services.JwtService
import com.example.crm.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

@DisplayName("Auth Controller Tests")
class AuthControllerTest {

    private lateinit var mockJwtService: JwtService
    private lateinit var mockUserService: UserService
    private lateinit var authController: AuthController
    private lateinit var mockCall: ApplicationCall

    @BeforeEach
    fun setUp() {
        mockJwtService = mockk()
        mockUserService = mockk()
        authController = AuthController(mockJwtService, mockUserService)
        mockCall = mockk(relaxed = true)
    }

    @Test
    @DisplayName("Should return token for valid credentials")
    fun testLoginWithValidCredentials() = runBlocking {
        // Given
        val username = "testuser"
        val password = "password123"
        val userId = UUID.randomUUID()
        val user = User(userId, username, "hashedpassword")
        val loginRequest = LoginRequest(username, password)
        val validToken = "valid.jwt.token"

        coEvery { mockCall.receive<LoginRequest>() } returns loginRequest
        coEvery { mockJwtService.createJwtToken(loginRequest) } returns validToken
        coEvery { mockUserService.findByUsername(username) } returns user

        // When
        authController.login(mockCall)

        // Then
        coVerify { mockCall.receive<LoginRequest>() }
        coVerify { mockJwtService.createJwtToken(loginRequest) }
        coVerify { mockUserService.findByUsername(username) }
        coVerify { mockCall.respond(hashMapOf("token" to validToken)) }
    }

    @Test
    @DisplayName("Should return 401 for invalid credentials")
    fun testLoginWithInvalidCredentials() = runBlocking {
        // Given
        val loginRequest = LoginRequest("testuser", "wrongpassword")

        coEvery { mockCall.receive<LoginRequest>() } returns loginRequest
        coEvery { mockJwtService.createJwtToken(loginRequest) } returns null
        coEvery { mockUserService.findByUsername("testuser") } returns null

        // When
        authController.login(mockCall)

        // Then
        coVerify { mockCall.receive<LoginRequest>() }
        coVerify { mockJwtService.createJwtToken(loginRequest) }
        coVerify { mockUserService.findByUsername("testuser") }
        coVerify { mockCall.respond(HttpStatusCode.Unauthorized, "Invalid credentials") }
    }

    @Test
    @DisplayName("Should return 401 when user not found")
    fun testLoginWithNonExistentUser() = runBlocking {
        // Given
        val loginRequest = LoginRequest("nonexistent", "password123")

        coEvery { mockCall.receive<LoginRequest>() } returns loginRequest
        coEvery { mockJwtService.createJwtToken(loginRequest) } returns null
        coEvery { mockUserService.findByUsername("nonexistent") } returns null

        // When
        authController.login(mockCall)

        // Then
        coVerify { mockCall.receive<LoginRequest>() }
        coVerify { mockJwtService.createJwtToken(loginRequest) }
        coVerify { mockUserService.findByUsername("nonexistent") }
        coVerify { mockCall.respond(HttpStatusCode.Unauthorized, "Invalid credentials") }
    }

    @Test
    @DisplayName("Should return 400 for malformed JSON")
    fun testLoginWithMalformedJson() = runBlocking {
        // Given
        coEvery { mockCall.receive<LoginRequest>() } throws RuntimeException("JSON parsing failed")

        // When
        authController.login(mockCall)

        // Then
        coVerify { mockCall.receive<LoginRequest>() }
        coVerify { mockCall.respond(HttpStatusCode.BadRequest, "Invalid request format") }
        // Should not call services when JSON parsing fails
        coVerify(exactly = 0) { mockJwtService.createJwtToken(any()) }
        coVerify(exactly = 0) { mockUserService.findByUsername(any()) }
    }

    @Test
    @DisplayName("Should handle empty username and password")
    fun testLoginWithEmptyCredentials() = runBlocking {
        // Given
        val loginRequest = LoginRequest("", "")

        coEvery { mockCall.receive<LoginRequest>() } returns loginRequest
        coEvery { mockJwtService.createJwtToken(loginRequest) } returns null
        coEvery { mockUserService.findByUsername("") } returns null

        // When
        authController.login(mockCall)

        // Then
        coVerify { mockCall.receive<LoginRequest>() }
        coVerify { mockJwtService.createJwtToken(loginRequest) }
        coVerify { mockUserService.findByUsername("") }
        coVerify { mockCall.respond(HttpStatusCode.Unauthorized, "Invalid credentials") }
    }
}