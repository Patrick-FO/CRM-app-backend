package com.example.crm.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.crm.routing.request.LoginRequest
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import kotlinx.coroutines.coroutineScope
import java.util.*

class JwtService(
    private val application: Application,
    private val userService: UserService
) {
    private val secret = getConfigProperty("jwt.secret")
    private val issuer = getConfigProperty("jwt.issuer")
    private val audience = getConfigProperty("jwt.audience")
    val realm = getConfigProperty("jwt.realm")

    val jwtVerifier: JWTVerifier =
        JWT
            .require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()

    suspend fun createJwtToken(loginRequest: LoginRequest): String? {
        val foundUser = userService.findByUsername(loginRequest.username)

        return if(foundUser != null && foundUser.password == loginRequest.password) {
            JWT
                .create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("username", foundUser.username)
                .withClaim("userId", foundUser.id.toString())
                .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000))
                .sign(Algorithm.HMAC256(secret))
        } else null
    }

    fun createAdminToken(): String {
        return JWT
            .create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("username", "admin-cli")
            .withClaim("userId", "00000000-0000-0000-0000-000000000000")
            .withClaim("role", "admin")
            .withExpiresAt(Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000)) // 1 year
            .sign(Algorithm.HMAC256(secret))
    }

    fun customValidator(credential: JWTCredential): JWTPrincipal? {
        val username = extractUsername(credential)

        //val foundUser = username?.let { userService::findByUsername }
        val foundUser = username?.let {
            kotlinx.coroutines.runBlocking { userService.findByUsername(it) }
        }

        return if(foundUser != null && audienceMatches(credential)) {
            JWTPrincipal(credential.payload)
        } else null
    }

    fun adminValidator(credential: JWTCredential): JWTPrincipal? {
        val role = credential.payload.getClaim("role")?.asString()
        val username = extractUsername(credential)

        return if(role == "admin" && username == "admin-cli" && audienceMatches(credential)) {
            JWTPrincipal(credential.payload)
        } else null
    }

    private fun audienceMatches(credential: JWTCredential): Boolean =
        credential.payload.audience.contains(audience)

    private fun extractUsername(credential: JWTCredential): String? =
        credential.payload.getClaim("username").asString()


    private fun getConfigProperty(path: String) =
        application.environment.config.property(path).getString()
}