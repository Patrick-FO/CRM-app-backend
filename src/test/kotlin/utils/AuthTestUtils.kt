package com.example.crm.utils

import com.example.crm.models.User
import com.example.crm.services.JwtService
import io.ktor.client.request.*
import io.ktor.http.*

object AuthTestUtils {
    fun createBearerToken(token: String) = "Bearer $token"

    fun HttpRequestBuilder.authenticateWith(token: String) {
        header("Authorization", createBearerToken(token))
    }

    suspend fun createUserAndGetToken(
        jwtService: JwtService,
        user: User = TestDataFactory.createTestUser()
    ): Pair<User, String> {
        // Implementation would create user and return token
        val token = jwtService.createJwtToken(
            com.example.crm.routing.request.LoginRequest(user.username, "password123")
        )
        return user to (token ?: throw Exception("Failed to create token"))
    }
}