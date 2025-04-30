package com.example.crm.services.repositories

import com.example.crm.models.Note

/**
 * Interface for note data operations
 */
interface NoteRepository {
    /**
     * Find all notes belonging to a specific user
     * @param userId The ID of the user whose notes to retrieve
     * @return List of notes for the specified user
     */
    suspend fun findByUserId(userId: Int): List<Note>

    /**
     * Find all notes related to a specific contact
     * @param userId The ID of the user
     * @param contactId The ID of the contact
     * @return List of notes related to the specified contact
     */
    suspend fun findByContact(userId: Int, contactId: Int): List<Note>

    /**
     * Find a specific note by ID
     * @param id The ID of the note to find
     * @return The note if found, null otherwise
     */
    suspend fun findById(id: Int): Note?

    /**
     * Create a new note
     * @param userId The ID of the user this note belongs to
     * @param title The title of the note
     * @param description The content of the note (optional)
     * @param contactIds Array of contact IDs this note relates to (optional)
     * @return The newly created note
     */
    suspend fun create(
        userId: Int,
        title: String,
        description: String? = null,
        contactIds: Array<Int>? = null
    ): Note

    /**
     * Update an existing note
     * @param id The ID of the note to update
     * @param title The new title (or null if unchanged)
     * @param description The new description (or null if unchanged)
     * @param contactIds The new array of contact IDs (or null if unchanged)
     * @return True if update was successful, false otherwise
     */
    suspend fun update(
        id: Int,
        title: String? = null,
        description: String? = null,
        contactIds: Array<Int>? = null
    ): Boolean

    /**
     * Delete a note by ID
     * @param id The ID of the note to delete
     * @return True if deletion was successful, false otherwise
     */
    suspend fun delete(id: Int): Boolean
}