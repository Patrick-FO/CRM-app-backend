/*package com.example.crm.fixtures

import com.example.crm.models.Contact
import com.example.crm.models.Note
import com.example.crm.models.User
import org.mindrot.jbcrypt.BCrypt
import java.util.*


object TestUsers {
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
}

object TestContacts {
    val JOHN_DOE = Contact(
        id = 1,
        userId = TestUsers.VALID_USER.id,
        name = "John Doe",
        company = "Tech Corporation",
        phoneNumber = "+1234567890",
        contactEmail = "john.doe@techcorp.com"
    )

    val JANE_SMITH = Contact(
        id = 2,
        userId = TestUsers.VALID_USER.id,
        name = "Jane Smith",
        company = "Design Studio",
        phoneNumber = "+0987654321",
        contactEmail = "jane.smith@designstudio.com"
    )

    val BOB_WILSON = Contact(
        id = 3,
        userId = TestUsers.SECOND_USER.id, // Different user
        name = "Bob Wilson",
        company = "Marketing Inc",
        phoneNumber = "+1122334455",
        contactEmail = "bob.wilson@marketing.com"
    )

    val MINIMAL_CONTACT = Contact(
        id = 4,
        userId = TestUsers.VALID_USER.id,
        name = "Minimal Contact",
        company = null,
        phoneNumber = null,
        contactEmail = null
    )

    val CONTACT_WITH_LONG_NAME = Contact(
        id = 5,
        userId = TestUsers.VALID_USER.id,
        name = "This is a very long contact name that tests the system limits",
        company = "Long Name Corp",
        phoneNumber = "+1234567890",
        contactEmail = "longname@longnamecorp.com"
    )
}

object TestNotes {
    val MEETING_NOTE = Note(
        id = 1,
        userId = TestUsers.VALID_USER.id,
        contactIds = listOf(1, 2),
        title = "Team Meeting Notes",
        description = "Discussed Q4 goals and project timelines. Action items assigned."
    )

    val FOLLOW_UP_NOTE = Note(
        id = 2,
        userId = TestUsers.VALID_USER.id,
        contactIds = listOf(1),
        title = "Follow-up Required",
        description = "Need to send proposal by end of week."
    )

    val GENERAL_NOTE = Note(
        id = 3,
        userId = TestUsers.VALID_USER.id,
        contactIds = emptyList(),
        title = "General Reminder",
        description = "Update CRM system documentation"
    )

    val OTHER_USER_NOTE = Note(
        id = 4,
        userId = TestUsers.SECOND_USER.id, // Different user
        contactIds = listOf(3),
        title = "Private Note",
        description = "This note belongs to a different user"
    )

    val NOTE_WITH_NULL_DESCRIPTION = Note(
        id = 5,
        userId = TestUsers.VALID_USER.id,
        contactIds = listOf(1),
        title = "Title Only Note",
        description = null
    )

    val LONG_NOTE = Note(
        id = 6,
        userId = TestUsers.VALID_USER.id,
        contactIds = listOf(1, 2),
        title = "Detailed Project Analysis",
        description = """
            This is a very long note that contains detailed information about the project.
            It includes multiple paragraphs, technical details, and comprehensive analysis
            that would be typical in a real-world CRM system. This helps test how the
            system handles longer content and ensures proper database storage and retrieval
            of larger text fields.
            
            The note also contains various formatting and special characters to test
            proper encoding and decoding: áéíóú, @#$%&*(), 123456789, etc.
        """.trimIndent()
    )
}

object TestConstants {
    const val VALID_PASSWORD = "password123"
    const val ADMIN_PASSWORD = "adminpass"
    const val INVALID_PASSWORD = "wrongpassword"

    const val VALID_JWT_SECRET = "test-jwt-secret-key-for-testing"
    const val JWT_ISSUER = "test-crm-app"
    const val JWT_AUDIENCE = "test-crm-users"
    const val JWT_REALM = "Test CRM Application"

    const val TEST_EMAIL = "test@example.com"
    const val INVALID_EMAIL = "invalid-email-format"
    const val TEST_PHONE = "+1234567890"

    const val TEST_DATABASE_URL = "jdbc:h2:mem:test_db;DB_CLOSE_DELAY=-1"
    const val TEST_DATABASE_DRIVER = "org.h2.Driver"
}

/**
 * Helper object for creating collections of test data
 */
object TestCollections {
    val ALL_USERS = listOf(
        TestUsers.VALID_USER,
        TestUsers.SECOND_USER,
        TestUsers.ADMIN_USER
    )

    val VALID_USER_CONTACTS = listOf(
        TestContacts.JOHN_DOE,
        TestContacts.JANE_SMITH,
        TestContacts.MINIMAL_CONTACT
    )

    val ALL_CONTACTS = listOf(
        TestContacts.JOHN_DOE,
        TestContacts.JANE_SMITH,
        TestContacts.BOB_WILSON,
        TestContacts.MINIMAL_CONTACT
    )

    val VALID_USER_NOTES = listOf(
        TestNotes.MEETING_NOTE,
        TestNotes.FOLLOW_UP_NOTE,
        TestNotes.GENERAL_NOTE
    )

    val ALL_NOTES = listOf(
        TestNotes.MEETING_NOTE,
        TestNotes.FOLLOW_UP_NOTE,
        TestNotes.GENERAL_NOTE,
        TestNotes.OTHER_USER_NOTE
    )
}*/