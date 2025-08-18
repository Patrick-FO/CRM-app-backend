package com.example.crm.unit.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.crm.models.User
import com.example.crm.routing.request.LoginRequest
import com.example.crm.services.JwtService
import com.example.crm.services.UserService
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mindrot.jbcrypt.BCrypt
import java.util.*

@DisplayName("JWT Service Tests")
class JwtServiceTest {

    private val testSecret = "test-secret-key"
    private val testIssuer = "test-issuer"
    private val testAudience = "test-audience"
    private val testRealm = "test-realm"

    private lateinit var mockApplication: Application
    private lateinit var mockUserService: UserService
    private lateinit var mockConfig: ApplicationConfig
    private lateinit var jwtService: JwtService

    @BeforeEach
    fun setUp() {
        // Setup mocks
        mockApplication = mockk()
        mockUserService = mockk()
        mockConfig = mockk()

        // Mock the configuration reading
        val mockEnvironment = mockk<ApplicationEnvironment>()
        every { mockApplication.environment } returns mockEnvironment
        every { mockEnvironment.config } returns mockConfig

        // Mock config properties
        every { mockConfig.property("jwt.secret").getString() } returns testSecret
        every { mockConfig.property("jwt.issuer").getString() } returns testIssuer
        every { mockConfig.property("jwt.audience").getString() } returns testAudience
        every { mockConfig.property("jwt.realm").getString() } returns testRealm

        // Create JwtService with mocked dependencies
        jwtService = JwtService(mockApplication, mockUserService)
    }

    @Test
    @DisplayName("Should return token for valid user credentials")
    fun testCreateJwtTokenWithValidCredentials() {
        // Given
        val username = "testuser"
        val password = "password123"
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        val userId = UUID.randomUUID()
        val user = User(userId, username, hashedPassword)
        val loginRequest = LoginRequest(username, password)

        coEvery { mockUserService.findByUsername(username) } returns user

        // When
        val token = runBlocking { jwtService.createJwtToken(loginRequest) }

        // Then
        assertNotNull(token, "Token should not be null for valid credentials")

        // Verify token contents
        val decodedJWT = JWT.require(Algorithm.HMAC256(testSecret))
            .withAudience(testAudience)
            .withIssuer(testIssuer)
            .build()
            .verify(token)

        assertEquals(username, decodedJWT.getClaim("username").asString())
        assertEquals(userId.toString(), decodedJWT.getClaim("userId").asString())
        assertTrue(decodedJWT.audience.contains(testAudience))
        assertEquals(testIssuer, decodedJWT.issuer)
    }

    @Test
    @DisplayName("Should return null when user not found")
    fun testCreateJwtTokenWithNonExistentUser() {
        // Given
        val loginRequest = LoginRequest("nonexistent", "password")
        coEvery { mockUserService.findByUsername("nonexistent") } returns null

        // When
        val token = runBlocking { jwtService.createJwtToken(loginRequest) }

        // Then
        assertNull(token, "Token should be null when user doesn't exist")
    }

    @Test
    @DisplayName("Should return null when password is incorrect")
    fun testCreateJwtTokenWithIncorrectPassword() {
        // Given
        val username = "testuser"
        val correctPassword = "correctpassword"
        val wrongPassword = "wrongpassword"
        val hashedPassword = BCrypt.hashpw(correctPassword, BCrypt.gensalt())
        val user = User(UUID.randomUUID(), username, hashedPassword)
        val loginRequest = LoginRequest(username, wrongPassword)

        coEvery { mockUserService.findByUsername(username) } returns user

        // When
        val token = runBlocking { jwtService.createJwtToken(loginRequest) }

        // Then
        assertNull(token, "Token should be null when password is incorrect")
    }

    @Test
    @DisplayName("Should create token that expires in 1 hour")
    fun testTokenExpirationTime() {
        // Given
        val user = User(UUID.randomUUID(), "testuser", BCrypt.hashpw("password", BCrypt.gensalt()))
        val loginRequest = LoginRequest("testuser", "password")
        coEvery { mockUserService.findByUsername("testuser") } returns user

        // When
        val startTime = System.currentTimeMillis()
        val token = runBlocking { jwtService.createJwtToken(loginRequest) }
        val endTime = System.currentTimeMillis()

        // Then
        assertNotNull(token, "Token should be created")
        val decodedJWT = JWT.require(Algorithm.HMAC256(testSecret)).build().verify(token)
        val expirationTime = decodedJWT.expiresAt.time

        // Token should expire between 59-61 minutes from now (allowing for test execution time)
        val expectedMinExpiration = startTime + (59 * 60 * 1000)
        val expectedMaxExpiration = endTime + (61 * 60 * 1000)

        assertTrue(expirationTime >= expectedMinExpiration, "Token should expire at least 59 minutes from now")
        assertTrue(expirationTime <= expectedMaxExpiration, "Token should expire at most 61 minutes from now")
    }

    @Test
    @DisplayName("Should create valid admin token")
    fun testCreateAdminToken() {
        // When
        val adminToken = jwtService.createAdminToken()

        // Then
        assertNotNull(adminToken, "Admin token should not be null")

        val decodedJWT = JWT.require(Algorithm.HMAC256(testSecret))
            .withAudience(testAudience)
            .withIssuer(testIssuer)
            .build()
            .verify(adminToken)

        assertEquals("admin-cli", decodedJWT.getClaim("username").asString())
        assertEquals("00000000-0000-0000-0000-000000000000", decodedJWT.getClaim("userId").asString())
        assertEquals("admin", decodedJWT.getClaim("role").asString())
    }

    @Test
    @DisplayName("Should create admin token that expires in 1 year")
    fun testAdminTokenExpiration() {
        // When
        val startTime = System.currentTimeMillis()
        val adminToken = jwtService.createAdminToken()

        // Then
        val decodedJWT = JWT.require(Algorithm.HMAC256(testSecret)).build().verify(adminToken)
        val expirationTime = decodedJWT.expiresAt.time
        val oneYearInMs = 365L * 24 * 60 * 60 * 1000

        // Should expire approximately 1 year from now (allowing 1 second tolerance)
        assertTrue(
            expirationTime - startTime >= oneYearInMs - 1000,
            "Admin token should expire approximately 1 year from now"
        )
    }

    @Test
    @DisplayName("Should validate token for existing user")
    fun testCustomValidatorWithValidUser() {
        // Given
        val username = "testuser"
        val user = User(UUID.randomUUID(), username, "hashedpassword")

        // Create a valid JWT credential
        val token = JWT.create()
            .withAudience(testAudience)
            .withIssuer(testIssuer)
            .withClaim("username", username)
            .sign(Algorithm.HMAC256(testSecret))

        val jwt = JWT.require(Algorithm.HMAC256(testSecret)).build().verify(token)
        val credential = mockk<JWTCredential>()
        every { credential.payload } returns jwt

        coEvery { mockUserService.findByUsername(username) } returns user

        // When
        val principal = jwtService.customValidator(credential)

        // Then
        assertNotNull(principal, "Principal should not be null for valid user")
        assertEquals(username, principal!!.payload.getClaim("username").asString())
    }

    @Test
    @DisplayName("Should reject token for non-existent user")
    fun testCustomValidatorWithNonExistentUser() {
        // Given
        val username = "nonexistentuser"

        val token = JWT.create()
            .withAudience(testAudience)
            .withIssuer(testIssuer)
            .withClaim("username", username)
            .sign(Algorithm.HMAC256(testSecret))

        val jwt = JWT.require(Algorithm.HMAC256(testSecret)).build().verify(token)
        val credential = mockk<JWTCredential>()
        every { credential.payload } returns jwt

        coEvery { mockUserService.findByUsername(username) } returns null

        // When
        val principal = jwtService.customValidator(credential)

        // Then
        assertNull(principal, "Principal should be null for non-existent user")
    }

    @Test
    @DisplayName("Should reject token with wrong audience")
    fun testCustomValidatorWithWrongAudience() {
        // Given
        val username = "testuser"
        val user = User(UUID.randomUUID(), username, "hashedpassword")

        // Create token with wrong audience
        val token = JWT.create()
            .withAudience("wrong-audience")
            .withIssuer(testIssuer)
            .withClaim("username", username)
            .sign(Algorithm.HMAC256(testSecret))

        val jwt = JWT.require(Algorithm.HMAC256(testSecret))
            .acceptLeeway(1000)
            .build()
            .verify(token)
        val credential = mockk<JWTCredential>()
        every { credential.payload } returns jwt

        coEvery { mockUserService.findByUsername(username) } returns user

        // When
        val principal = jwtService.customValidator(credential)

        // Then
        assertNull(principal, "Principal should be null for wrong audience")
    }

    @Test
    @DisplayName("Should validate admin token")
    fun testAdminValidator() {
        // Given
        val token = JWT.create()
            .withAudience(testAudience)
            .withIssuer(testIssuer)
            .withClaim("username", "admin-cli")
            .withClaim("role", "admin")
            .sign(Algorithm.HMAC256(testSecret))

        val jwt = JWT.require(Algorithm.HMAC256(testSecret)).build().verify(token)
        val credential = mockk<JWTCredential>()
        every { credential.payload } returns jwt

        // When
        val principal = jwtService.adminValidator(credential)

        // Then
        assertNotNull(principal, "Principal should not be null for valid admin token")
        assertEquals("admin", principal!!.payload.getClaim("role").asString())
        assertEquals("admin-cli", principal.payload.getClaim("username").asString())
    }

    @Test
    @DisplayName("Should reject non-admin token")
    fun testAdminValidatorWithRegularUser() {
        // Given
        val token = JWT.create()
            .withAudience(testAudience)
            .withIssuer(testIssuer)
            .withClaim("username", "regular-user")
            .withClaim("role", "user")
            .sign(Algorithm.HMAC256(testSecret))

        val jwt = JWT.require(Algorithm.HMAC256(testSecret)).build().verify(token)
        val credential = mockk<JWTCredential>()
        every { credential.payload } returns jwt

        // When
        val principal = jwtService.adminValidator(credential)

        // Then
        assertNull(principal, "Principal should be null for non-admin token")
    }

    @Test
    @DisplayName("Should reject admin token with wrong username")
    fun testAdminValidatorWithWrongUsername() {
        // Given
        val token = JWT.create()
            .withAudience(testAudience)
            .withIssuer(testIssuer)
            .withClaim("username", "fake-admin")
            .withClaim("role", "admin")
            .sign(Algorithm.HMAC256(testSecret))

        val jwt = JWT.require(Algorithm.HMAC256(testSecret)).build().verify(token)
        val credential = mockk<JWTCredential>()
        every { credential.payload } returns jwt

        // When
        val principal = jwtService.adminValidator(credential)

        // Then
        assertNull(principal, "Principal should be null for admin token with wrong username")
    }

    @Test
    @DisplayName("Should verify valid tokens")
    fun testJwtVerifierWithValidToken() {
        // Given
        val token = JWT.create()
            .withAudience(testAudience)
            .withIssuer(testIssuer)
            .withClaim("username", "testuser")
            .sign(Algorithm.HMAC256(testSecret))

        // When & Then (should not throw exception)
        val decodedJWT = jwtService.jwtVerifier.verify(token)
        assertEquals("testuser", decodedJWT.getClaim("username").asString())
    }

    @Test
    @DisplayName("Should reject tokens with wrong signature")
    fun testJwtVerifierWithInvalidToken() {
        // Given
        val token = JWT.create()
            .withAudience(testAudience)
            .withIssuer(testIssuer)
            .withClaim("username", "testuser")
            .sign(Algorithm.HMAC256("wrong-secret"))

        // When & Then
        assertThrows(Exception::class.java) {
            jwtService.jwtVerifier.verify(token)
        }
    }

    @Test
    @DisplayName("Should handle null username in token validation")
    fun testCustomValidatorWithNullUsername() {
        // Given
        val token = JWT.create()
            .withAudience(testAudience)
            .withIssuer(testIssuer)
            // No username claim
            .sign(Algorithm.HMAC256(testSecret))

        val jwt = JWT.require(Algorithm.HMAC256(testSecret)).build().verify(token)
        val credential = mockk<JWTCredential>()
        every { credential.payload } returns jwt

        // When
        val principal = jwtService.customValidator(credential)

        // Then
        assertNull(principal, "Principal should be null when username is missing")
    }
}