package com.example.crm.unit.controllers

import com.example.crm.controllers.UserController
import com.example.crm.models.User
import com.example.crm.routing.request.UserRequest
import com.example.crm.routing.response.UserResponse
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

@DisplayName("User Controller Tests")
class UserControllerTest {

    private lateinit var mockUserService: UserService
    private lateinit var userController: UserController
    private lateinit var mockCall: ApplicationCall

    @BeforeEach
    fun setUp() {
        mockUserService = mockk()
        userController = UserController(mockUserService)
        mockCall = mockk(relaxed = true)
    }

    @Test
    @DisplayName("Should create user with valid data")
    fun testCreateUserWithValidData() = runBlocking {
        // Given
        val userRequest = UserRequest("newuser", "password123")
        val userId = UUID.randomUUID()
        val createdUser = User(userId, "newuser", "password123")

        coEvery { mockCall.receive<UserRequest>() } returns userRequest
        coEvery { mockUserService.save(any()) } returns createdUser

        // When
        userController.createUser(mockCall)

        // Then
        coVerify { mockUserService.save(any()) }
        coVerify { mockCall.respond(HttpStatusCode.Created) }
    }

    @Test
    @DisplayName("Should return 400 when user already exists")
    fun testCreateUserWithDuplicateUsername() = runBlocking {
        // Given
        val userRequest = UserRequest("existinguser", "password123")

        coEvery { mockCall.receive<UserRequest>() } returns userRequest
        coEvery { mockUserService.save(any()) } returns null

        // When
        userController.createUser(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.BadRequest, "User already exists") }
    }

    @Test
    @DisplayName("Should return 400 for malformed request")
    fun testCreateUserWithMalformedRequest() = runBlocking {
        // Given
        coEvery { mockCall.receive<UserRequest>() } throws RuntimeException("JSON parsing failed")

        // When
        userController.createUser(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.BadRequest, "Invalid request format") }
    }

    @Test
    @DisplayName("Should return all users")
    fun testGetAllUsers() = runBlocking {
        // Given
        val users = listOf(
            User(UUID.randomUUID(), "user1", "hashedpassword1"),
            User(UUID.randomUUID(), "user2", "hashedpassword2")
        )

        coEvery { mockUserService.findAll() } returns users

        // When
        userController.getAllUsers(mockCall)

        // Then
        coVerify { mockUserService.findAll() }
        coVerify { mockCall.respond(any<List<UserResponse>>()) }
    }

    @Test
    @DisplayName("Should handle service exception in getAllUsers")
    fun testGetAllUsersWithServiceException() = runBlocking {
        // Given
        coEvery { mockUserService.findAll() } throws RuntimeException("Database error")

        // When
        userController.getAllUsers(mockCall)

        // Then
        coVerify { mockCall.respond(HttpStatusCode.InternalServerError, "Failed to fetch users") }
    }
}