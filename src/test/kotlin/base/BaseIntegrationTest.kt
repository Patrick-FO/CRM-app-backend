package com.example.crm.base

import com.example.crm.models.User
import com.example.crm.routing.request.ContactRequest
import com.example.crm.routing.request.LoginRequest
import com.example.crm.routing.request.NoteRequest
import com.example.crm.routing.request.UserRequest
import com.example.crm.services.JwtService
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.After
import org.junit.Before
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.ktor.ext.inject
import java.util.*

abstract class BaseIntegrationTest {

    protected lateinit var testUser: User
    protected lateinit var testUserToken: String
    protected lateinit var adminToken: String

    @Before
    fun setUp() {
        // Clear any existing Koin context
        stopKoin()
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    protected fun ApplicationTestBuilder.setupTestData() {
        val jwtService by application.inject<JwtService>()

        // Create test user
        testUser = User(
            id = UUID.randomUUID(),
            username = "testuser",
            password = "password123"
        )

        // Create admin token
        adminToken = jwtService.createAdminToken()

        // Create test user and get token
        client.post("/api/user") {
            contentType(ContentType.Application.Json)
            setBody(UserRequest(testUser.username, "password123"))
        }

        // Login to get user token
        val loginResponse = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(testUser.username, "password123"))
        }

        val tokenMap = loginResponse.body<Map<String, String>>()
        testUserToken = tokenMap["token"]!!
    }

    protected fun ApplicationTestBuilder.createTestClient() = createClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    protected fun ApplicationTestBuilder.clearDatabase() {
        transaction {
            SchemaUtils.drop(
                com.example.crm.database.Notes,
                com.example.crm.database.Contacts,
                com.example.crm.database.Users
            )
            SchemaUtils.create(
                com.example.crm.database.Users,
                com.example.crm.database.Contacts,
                com.example.crm.database.Notes
            )
        }
    }
}

// Test data builders
object TestDataBuilder {
    fun createUserRequest(
        username: String = "testuser${UUID.randomUUID()}",
        password: String = "password123"
    ) = UserRequest(username, password)

    fun createContactRequest(
        name: String = "John Doe",
        company: String? = "Test Company",
        phoneNumber: String? = "+1234567890",
        contactEmail: String? = "john@test.com"
    ) = ContactRequest(name, company, phoneNumber, contactEmail)

    fun createNoteRequest(
        contactIds: List<Int> = emptyList(),
        title: String = "Test Note",
        description: String? = "Test description"
    ) = NoteRequest(contactIds, title, description)

    fun createLoginRequest(
        username: String = "testuser",
        password: String = "password123"
    ) = LoginRequest(username, password)
}