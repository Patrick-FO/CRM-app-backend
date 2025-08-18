package com.example.crm.fixtures

import com.example.crm.models.Note
import java.util.*

object NoteFixtures {
    // Standard test notes
    val MEETING_NOTE = Note(
        id = 1,
        userId = UserFixtures.VALID_USER.id,
        contactIds = listOf(1, 2), // References JOHN_DOE and JANE_SMITH
        title = "Team Meeting Notes",
        description = "Discussed Q4 goals and project timelines. Action items assigned."
    )

    val FOLLOW_UP_NOTE = Note(
        id = 2,
        userId = UserFixtures.VALID_USER.id,
        contactIds = listOf(1), // References JOHN_DOE
        title = "Follow-up Required",
        description = "Need to send proposal by end of week."
    )

    val GENERAL_NOTE = Note(
        id = 3,
        userId = UserFixtures.VALID_USER.id,
        contactIds = emptyList(), // Not associated with any contact
        title = "General Reminder",
        description = "Update CRM system documentation"
    )

    val OTHER_USER_NOTE = Note(
        id = 4,
        userId = UserFixtures.SECOND_USER.id, // Different user
        contactIds = listOf(3), // References BOB_WILSON
        title = "Private Note",
        description = "This note belongs to a different user"
    )

    // Edge case notes
    val NOTE_WITH_NULL_DESCRIPTION = Note(
        id = 5,
        userId = UserFixtures.VALID_USER.id,
        contactIds = listOf(1),
        title = "Title Only Note",
        description = null
    )

    val LONG_NOTE = Note(
        id = 6,
        userId = UserFixtures.VALID_USER.id,
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
            
            Additional testing content:
            - Bullet points and lists
            - Special characters: ñ, ç, ü, ß
            - Numbers and symbols: 12345, !@#$%^&*()
            - Unicode characters: 🚀 📊 💼
        """.trimIndent()
    )

    val NOTE_WITH_MANY_CONTACTS = Note(
        id = 7,
        userId = UserFixtures.VALID_USER.id,
        contactIds = listOf(1, 2, 4, 5), // Multiple contact associations
        title = "Multi-Contact Meeting",
        description = "Group discussion involving multiple stakeholders"
    )

    val URGENT_NOTE = Note(
        id = 8,
        userId = UserFixtures.VALID_USER.id,
        contactIds = listOf(1),
        title = "URGENT: Client Issue",
        description = "Critical issue reported by client. Needs immediate attention!"
    )

    val EMPTY_TITLE_NOTE = Note(
        id = 9,
        userId = UserFixtures.VALID_USER.id,
        contactIds = listOf(2),
        title = "",
        description = "Note with empty title for testing edge cases"
    )

    // Collections for easy access
    val VALID_USER_NOTES = listOf(
        MEETING_NOTE,
        FOLLOW_UP_NOTE,
        GENERAL_NOTE,
        NOTE_WITH_NULL_DESCRIPTION,
        LONG_NOTE,
        NOTE_WITH_MANY_CONTACTS,
        URGENT_NOTE,
        EMPTY_TITLE_NOTE
    )

    val SECOND_USER_NOTES = listOf(
        OTHER_USER_NOTE
    )

    val ALL_NOTES = VALID_USER_NOTES + SECOND_USER_NOTES

    val NOTES_WITH_CONTACTS = listOf(
        MEETING_NOTE,
        FOLLOW_UP_NOTE,
        NOTE_WITH_NULL_DESCRIPTION,
        LONG_NOTE,
        NOTE_WITH_MANY_CONTACTS,
        URGENT_NOTE,
        EMPTY_TITLE_NOTE
    )

    val NOTES_WITHOUT_CONTACTS = listOf(
        GENERAL_NOTE
    )

    val NOTES_WITH_DESCRIPTIONS = listOf(
        MEETING_NOTE,
        FOLLOW_UP_NOTE,
        GENERAL_NOTE,
        OTHER_USER_NOTE,
        LONG_NOTE,
        NOTE_WITH_MANY_CONTACTS,
        URGENT_NOTE,
        EMPTY_TITLE_NOTE
    )

    val NOTES_WITHOUT_DESCRIPTIONS = listOf(
        NOTE_WITH_NULL_DESCRIPTION
    )

    // Helper functions for creating test notes
    fun createTestNote(
        id: Int = 999,
        userId: UUID = UserFixtures.VALID_USER.id,
        contactIds: List<Int> = emptyList(),
        title: String = "Test Note",
        description: String? = "Test description"
    ) = Note(
        id = id,
        userId = userId,
        contactIds = contactIds,
        title = title,
        description = description
    )

    fun createNoteWithContact(
        contactId: Int,
        userId: UUID = UserFixtures.VALID_USER.id,
        title: String = "Note for Contact",
        description: String? = "Test note description"
    ) = Note(
        id = 999,
        userId = userId,
        contactIds = listOf(contactId),
        title = title,
        description = description
    )

    fun createNoteWithMultipleContacts(
        contactIds: List<Int>,
        userId: UUID = UserFixtures.VALID_USER.id,
        title: String = "Multi-Contact Note"
    ) = Note(
        id = 999,
        userId = userId,
        contactIds = contactIds,
        title = title,
        description = "Note associated with multiple contacts"
    )

    fun createGeneralNote(
        userId: UUID = UserFixtures.VALID_USER.id,
        title: String = "General Note",
        description: String? = "General note not associated with any contact"
    ) = Note(
        id = 999,
        userId = userId,
        contactIds = emptyList(),
        title = title,
        description = description
    )

    fun createNotesForUser(userId: UUID, count: Int = 3): List<Note> {
        return (1..count).map { i ->
            Note(
                id = 2000 + i,
                userId = userId,
                contactIds = emptyList(),
                title = "Test Note $i",
                description = "Description for test note $i"
            )
        }
    }

    fun createNotesForContact(contactId: Int, userId: UUID, count: Int = 2): List<Note> {
        return (1..count).map { i ->
            Note(
                id = 3000 + i,
                userId = userId,
                contactIds = listOf(contactId),
                title = "Contact Note $i",
                description = "Note $i for contact $contactId"
            )
        }
    }
}

// Constants related to notes
object NoteConstants {
    // Common test content
    const val DEFAULT_TITLE = "Test Note"
    const val DEFAULT_DESCRIPTION = "Test description"
    const val URGENT_PREFIX = "URGENT:"
    const val MEETING_TITLE = "Meeting Notes"
    const val FOLLOW_UP_TITLE = "Follow-up Required"

    // Length limits
    const val MAX_TITLE_LENGTH = 200
    const val MAX_DESCRIPTION_LENGTH = 5000
    const val MIN_TITLE_LENGTH = 1

    // Test patterns
    val COMMON_NOTE_TITLES = listOf(
        "Meeting Notes",
        "Follow-up Required",
        "General Reminder",
        "Project Update",
        "Client Call",
        "Action Items",
        "Important Information"
    )

    val SAMPLE_DESCRIPTIONS = listOf(
        "Brief meeting summary",
        "Discussed project timeline and deliverables",
        "Client requested additional features",
        "Action items assigned to team members",
        "Follow-up scheduled for next week"
    )

    // Special characters for testing
    const val SPECIAL_CHARS_TEXT = "Testing special characters: áéíóú, ñ, ç, ü, ß, @#$%&*()"
    const val UNICODE_EMOJIS = "📊 📈 📉 💼 🚀 ✅ ❌ ⚠️"
    val LONG_TEXT_SAMPLE = """
        This is a sample of long text content that might be used in a real CRM note.
        It contains multiple lines, various punctuation marks, and represents the kind
        of detailed information that users might want to store about their contacts
        and business relationships.
    """.trimIndent()
}