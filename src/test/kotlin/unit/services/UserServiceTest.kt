package com.example.crm.unit.services

import com.example.crm.data.UserRepositoryImpl
import com.example.crm.models.User
import com.example.crm.services.UserService
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mindrot.jbcrypt.BCrypt
import java.util.*

@DisplayName("User Service Tests")
class UserServiceTest {

    private lateinit var mockUserRepository: UserRepositoryImpl
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        mockUserRepository = mockk()
        userService = UserService(mockUserRepository)
    }

    @Test
    @DisplayName("Should hash password when saving user")
    fun testSaveUserHashesPassword() = runBlocking {
        // Given
        val plainPassword = "password123"
        val user = User(UUID.randomUUID(), "testuser", plainPassword)

        every { runBlocking { mockUserRepository.save(any()) } } returns true
        coEvery { mockUserRepository.findByUsername("testuser") } returns null

        // When
        val result = userService.save(user)

        // Then
        assertNotNull(result, "Result should not be null")
        assertEquals("testuser", result!!.username)

        // Verify that the repository was called with a hashed password
        coVerify {
            mockUserRepository.save(match { savedUser ->
                savedUser.password != plainPassword &&
                        BCrypt.checkpw(plainPassword, savedUser.password)
            })
        }
    }

    @Test
    @DisplayName("Should not save user with duplicate username")
    fun testSaveUserWithDuplicateUsername() = runBlocking {
        // Given
        val existingUser = User(UUID.randomUUID(), "testuser", "hashedpassword")
        val newUser = User(UUID.randomUUID(), "testuser", "password123")

        coEvery { mockUserRepository.findByUsername("testuser") } returns existingUser

        // When
        val result = userService.save(newUser)

        // Then
        assertNull(result, "Should return null for duplicate username")

        // Verify repository save was never called
        coVerify(exactly = 0) { mockUserRepository.save(any()) }
    }

    @Test
    @DisplayName("Should find user by username")
    fun testFindByUsername() = runBlocking {
        // Given
        val user = User(UUID.randomUUID(), "testuser", "hashedpassword")
        coEvery { mockUserRepository.findByUsername("testuser") } returns user

        // When
        val result = userService.findByUsername("testuser")

        // Then
        assertNotNull(result, "User should be found")
        assertEquals("testuser", result!!.username)
        assertEquals(user.id, result.id)
    }

    @Test
    @DisplayName("Should return null for non-existent user")
    fun testFindNonExistentUser() = runBlocking {
        // Given
        coEvery { mockUserRepository.findByUsername("nonexistent") } returns null

        // When
        val result = userService.findByUsername("nonexistent")

        // Then
        assertNull(result, "Should return null for non-existent user")
    }

    @Test
    @DisplayName("Should find user by ID")
    fun testFindById() = runBlocking {
        // Given
        val userId = UUID.randomUUID()
        val user = User(userId, "testuser", "hashedpassword")
        coEvery { mockUserRepository.findById(userId) } returns user

        // When
        val result = userService.findById(userId.toString())

        // Then
        assertNotNull(result, "User should be found by ID")
        assertEquals(userId, result!!.id)
        assertEquals("testuser", result.username)
    }

    @Test
    @DisplayName("Should return null for invalid UUID format")
    fun testFindByInvalidUUID() = runBlocking {
        // When & Then
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking { userService.findById("invalid-uuid") }
        }
    }

    @Test
    @DisplayName("Should return all users")
    fun testFindAll() = runBlocking {
        // Given
        val users = listOf(
            User(UUID.randomUUID(), "user1", "password1"),
            User(UUID.randomUUID(), "user2", "password2")
        )
        coEvery { mockUserRepository.findAll() } returns users

        // When
        val result = userService.findAll()

        // Then
        assertEquals(2, result.size, "Should return all users")
        assertTrue(result.any { it.username == "user1" })
        assertTrue(result.any { it.username == "user2" })
    }

    @Test
    @DisplayName("Should handle repository errors gracefully")
    fun testRepositoryError() = runBlocking {
        // Given
        val user = User(UUID.randomUUID(), "testuser", "password123")
        coEvery { mockUserRepository.findByUsername("testuser") } returns null
        every { runBlocking { mockUserRepository.save(any()) } } returns false

        // When
        val result = userService.save(user)

        // Then
        assertEquals(user, result, "Should return original user even if save fails")
    }

    @Test
    @DisplayName("Should preserve user ID when saving")
    fun testSavePreservesUserId() = runBlocking {
        // Given
        val userId = UUID.randomUUID()
        val user = User(userId, "testuser", "password123")

        every { runBlocking { mockUserRepository.save(any()) } } returns true
        coEvery { mockUserRepository.findByUsername("testuser") } returns null

        // When
        val result = userService.save(user)

        // Then
        assertEquals(userId, result!!.id, "Should preserve original user ID")

        coVerify {
            mockUserRepository.save(match { it.id == userId })
        }
    }
}