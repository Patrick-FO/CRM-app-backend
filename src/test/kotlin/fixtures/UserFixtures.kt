package com.example.crm.fixtures

import com.example.crm.models.User
import org.mindrot.jbcrypt.BCrypt
import java.util.*

object UserFixtures {
    val VALID_USER = User(
        id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
        username = "testuser",
        password = BCrypt.hashpw("password123", BCrypt.gensalt())
    )

    val SECOND_USER = User(
        id = UUID.fromString("123e4567-e89b-12d3-a456-426614174001"),
        username = "testuser2",
        password = BCrypt.hashpw("password456", BCrypt.gensalt())
    )

    val ADMIN_USER = User(
        id = UUID.fromString("123e4567-e89b-12d3-a456-426614174002"),
        username = "admin",
        password = BCrypt.hashpw("adminpass", BCrypt.gensalt())
    )

    val USER_WITH_LONG_USERNAME = User(
        id = UUID.fromString("123e4567-e89b-12d3-a456-426614174003"),
        username = "very_long_username_that_tests_limits",
        password = BCrypt.hashpw("password789", BCrypt.gensalt())
    )

    // Collections for easy access
    val ALL_USERS = listOf(
        VALID_USER,
        SECOND_USER,
        ADMIN_USER,
        USER_WITH_LONG_USERNAME
    )

    val STANDARD_USERS = listOf(
        VALID_USER,
        SECOND_USER
    )

    // Helper functions for creating test users
    fun createTestUser(
        id: UUID = UUID.randomUUID(),
        username: String = "testuser_${System.currentTimeMillis()}",
        plainPassword: String = "password123"
    ) = User(
        id = id,
        username = username,
        password = BCrypt.hashpw(plainPassword, BCrypt.gensalt())
    )

    fun createUserWithPlainPassword(username: String, plainPassword: String) = User(
        id = UUID.randomUUID(),
        username = username,
        password = BCrypt.hashpw(plainPassword, BCrypt.gensalt())
    )
}

// Constants related to users
object UserConstants {
    const val VALID_PASSWORD = "password123"
    const val ADMIN_PASSWORD = "adminpass"
    const val INVALID_PASSWORD = "wrongpassword"
    const val SECOND_USER_PASSWORD = "password456"

    // Common test usernames
    const val DEFAULT_TEST_USERNAME = "testuser"
    const val ADMIN_USERNAME = "admin"
    const val SECOND_TEST_USERNAME = "testuser2"

    // Validation limits
    const val MIN_USERNAME_LENGTH = 3
    const val MAX_USERNAME_LENGTH = 50
    const val MIN_PASSWORD_LENGTH = 6
}