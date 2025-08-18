package com.example.crm.utils

import com.example.crm.models.Contact
import com.example.crm.models.Note
import com.example.crm.models.User
import com.example.crm.routing.request.ContactRequest
import com.example.crm.routing.request.LoginRequest
import com.example.crm.routing.request.NoteRequest
import com.example.crm.routing.request.UserRequest
import org.mindrot.jbcrypt.BCrypt
import java.util.*

object TestDataFactory {

    val TEST_USER_ID: UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
    val OTHER_USER_ID: UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174001")
    val ADMIN_USER_ID: UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174002")

    fun createTestUser(
        id: UUID = TEST_USER_ID,
        username: String = "testuser",
        password: String = "password123",
        hashPassword: Boolean = true
    ): User {
        val finalPassword = if (hashPassword) {
            BCrypt.hashpw(password, BCrypt.gensalt())
        } else {
            password
        }

        return User(
            id = id,
            username = username,
            password = finalPassword
        )
    }

    fun createAdminUser(): User {
        return User(
            id = ADMIN_USER_ID,
            username = "admin",
            password = BCrypt.hashpw("adminpass", BCrypt.gensalt())
        )
    }

    fun createValidUserRequest(
        username: String = "testuser",
        password: String = "password123"
    ): UserRequest {
        return UserRequest(username, password)
    }

    fun createTestContact(
        id: Int = 1,
        userId: UUID = TEST_USER_ID,
        name: String = "John Doe",
        company: String? = "Tech Corp",
        phoneNumber: String? = "+1234567890",
        email: String? = "john@techcorp.com"
    ): Contact {
        return Contact(
            id = id,
            userId = userId,
            name = name,
            company = company,
            phoneNumber = phoneNumber,
            contactEmail = email
        )
    }

    fun createContactRequest(
        name: String = "John Doe",
        company: String? = "Tech Corp",
        phoneNumber: String? = "+1234567890",
        email: String? = "john@techcorp.com"
    ): ContactRequest {
        return ContactRequest(name, company, phoneNumber, email)
    }

    fun createTestNote(
        id: Int = 1,
        userId: UUID = TEST_USER_ID,
        contactIds: List<Int> = listOf(1),
        title: String = "Meeting Notes",
        description: String? = "Discussed project requirements"
    ): Note {
        return Note(
            id = id,
            userId = userId,
            contactIds = contactIds,
            title = title,
            description = description
        )
    }

    fun createNoteRequest(
        title: String = "Meeting Notes",
        description: String? = "Discussed project requirements",
        contactIds: List<Int> = listOf(1)
    ): NoteRequest {
        return NoteRequest(title = title, description = description, contactIds = contactIds)
    }

    fun createLoginRequest(
        username: String = "testuser",
        password: String = "password123"
    ): LoginRequest {
        return LoginRequest(username, password)
    }

    fun createMultipleUsers(count: Int = 3): List<User> {
        return (1..count).map { i ->
            createTestUser(
                id = UUID.randomUUID(),
                username = "testuser$i",
                password = "password$i"
            )
        }
    }

    fun createMultipleContacts(userId: UUID = TEST_USER_ID, count: Int = 3): List<Contact> {
        return (1..count).map { i ->
            createTestContact(
                id = i,
                userId = userId,
                name = "Contact $i",
                company = "Company $i",
                email = "contact$i@company$i.com"
            )
        }
    }

    fun createMultipleNotes(userId: UUID = TEST_USER_ID, count: Int = 3): List<Note> {
        return (1..count).map { i ->
            createTestNote(
                id = i,
                userId = userId,
                title = "Note $i",
                description = "Description for note $i",
                contactIds = listOf(i)
            )
        }
    }

    fun createUserWithLongUsername(): User {
        return createTestUser(username = "a".repeat(50)) // Assuming 50 is max length
    }

    fun createContactWithMinimalData(): Contact {
        return createTestContact(
            company = null,
            phoneNumber = null,
            email = null
        )
    }

    fun createNoteWithEmptyContactIds(): Note {
        return createTestNote(contactIds = emptyList())
    }

    fun createInvalidLoginRequest(): LoginRequest {
        return LoginRequest("", "")
    }

    fun createContactWithInvalidEmail(): ContactRequest {
        return createContactRequest(email = "invalid-email")
    }

    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(password, hashedPassword)
    }
}