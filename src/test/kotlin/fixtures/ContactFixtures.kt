package com.example.crm.fixtures

import com.example.crm.models.Contact
import java.util.*

object ContactFixtures {
    // Standard test contacts
    val JOHN_DOE = Contact(
        id = 1,
        userId = UserFixtures.VALID_USER.id,
        name = "John Doe",
        company = "Tech Corporation",
        phoneNumber = "+1234567890",
        contactEmail = "john.doe@techcorp.com"
    )

    val JANE_SMITH = Contact(
        id = 2,
        userId = UserFixtures.VALID_USER.id,
        name = "Jane Smith",
        company = "Design Studio",
        phoneNumber = "+0987654321",
        contactEmail = "jane.smith@designstudio.com"
    )

    val BOB_WILSON = Contact(
        id = 3,
        userId = UserFixtures.SECOND_USER.id, // Different user
        name = "Bob Wilson",
        company = "Marketing Inc",
        phoneNumber = "+1122334455",
        contactEmail = "bob.wilson@marketing.com"
    )

    // Edge case contacts
    val MINIMAL_CONTACT = Contact(
        id = 4,
        userId = UserFixtures.VALID_USER.id,
        name = "Minimal Contact",
        company = null,
        phoneNumber = null,
        contactEmail = null
    )

    val CONTACT_WITH_LONG_NAME = Contact(
        id = 5,
        userId = UserFixtures.VALID_USER.id,
        name = "This is a very long contact name that tests the system limits",
        company = "Long Name Corp",
        phoneNumber = "+1234567890",
        contactEmail = "longname@longnamecorp.com"
    )

    val CONTACT_WITH_SPECIAL_CHARS = Contact(
        id = 6,
        userId = UserFixtures.VALID_USER.id,
        name = "José María García-Rodríguez",
        company = "International Corp & Associates",
        phoneNumber = "+34-91-123-4567",
        contactEmail = "jose.garcia@international.es"
    )

    val FREELANCER_CONTACT = Contact(
        id = 7,
        userId = UserFixtures.VALID_USER.id,
        name = "Sarah Connor",
        company = null, // Freelancer
        phoneNumber = "+1555123456",
        contactEmail = "sarah.freelance@gmail.com"
    )

    // Collections for easy access
    val VALID_USER_CONTACTS = listOf(
        JOHN_DOE,
        JANE_SMITH,
        MINIMAL_CONTACT,
        CONTACT_WITH_LONG_NAME,
        CONTACT_WITH_SPECIAL_CHARS,
        FREELANCER_CONTACT
    )

    val SECOND_USER_CONTACTS = listOf(
        BOB_WILSON
    )

    val ALL_CONTACTS = VALID_USER_CONTACTS + SECOND_USER_CONTACTS

    val CONTACTS_WITH_COMPANIES = listOf(
        JOHN_DOE,
        JANE_SMITH,
        BOB_WILSON,
        CONTACT_WITH_LONG_NAME,
        CONTACT_WITH_SPECIAL_CHARS
    )

    val CONTACTS_WITHOUT_COMPANIES = listOf(
        MINIMAL_CONTACT,
        FREELANCER_CONTACT
    )

    // Helper functions for creating test contacts
    fun createTestContact(
        id: Int = 999,
        userId: UUID = UserFixtures.VALID_USER.id,
        name: String = "Test Contact",
        company: String? = "Test Company",
        phoneNumber: String? = "+1234567890",
        contactEmail: String? = "test@example.com"
    ) = Contact(
        id = id,
        userId = userId,
        name = name,
        company = company,
        phoneNumber = phoneNumber,
        contactEmail = contactEmail
    )

    fun createMinimalContact(
        userId: UUID = UserFixtures.VALID_USER.id,
        name: String = "Minimal Test Contact"
    ) = Contact(
        id = 999,
        userId = userId,
        name = name,
        company = null,
        phoneNumber = null,
        contactEmail = null
    )

    fun createContactsForUser(userId: UUID, count: Int = 3): List<Contact> {
        return (1..count).map { i ->
            Contact(
                id = 1000 + i,
                userId = userId,
                name = "Test Contact $i",
                company = "Test Company $i",
                phoneNumber = "+123456789$i",
                contactEmail = "contact$i@test.com"
            )
        }
    }
}

// Constants related to contacts
object ContactConstants {
    const val TEST_EMAIL = "test@example.com"
    const val INVALID_EMAIL = "invalid-email-format"
    const val TEST_PHONE = "+1234567890"
    const val INVALID_PHONE = "not-a-phone-number"

    // Common test companies
    const val DEFAULT_COMPANY = "Test Company"
    const val TECH_COMPANY = "Tech Corporation"
    const val DESIGN_COMPANY = "Design Studio"

    // Validation patterns
    val VALID_EMAIL_PATTERN = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    val VALID_PHONE_PATTERN = Regex("^\\+?[1-9]\\d{1,14}$")

    // Length limits
    const val MAX_NAME_LENGTH = 100
    const val MAX_COMPANY_LENGTH = 100
    const val MAX_EMAIL_LENGTH = 255
    const val MAX_PHONE_LENGTH = 20
}