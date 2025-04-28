package com.example.crm.models.repositories

import com.example.crm.models.classes.Contact

/**
 * Interface for contact data operations
 */
interface ContactRepository {
    /**
     * Find all contacts belonging to a specific user
     * @param userId The ID of the user whose contacts to retrieve
     * @return List of contacts for the specified user
     */
    suspend fun findByUserId(userId: Int): List<Contact>

    /**
     * Find a specific contact by ID
     * @param id The ID of the contact to find
     * @return The contact if found, null otherwise
     */
    suspend fun findById(id: Int): Contact?

    /**
     * Create a new contact
     * @param userId The ID of the user this contact belongs to
     * @param name The name of the contact
     * @param company The company of the contact (optional)
     * @param phoneNumber The phone number of the contact (optional)
     * @param email The email of the contact (optional)
     * @return The newly created contact
     */
    suspend fun create(
        userId: Int,
        name: String,
        company: String? = null,
        phoneNumber: String? = null,
        email: String? = null
    ): Contact

    /**
     * Update an existing contact
     * @param id The ID of the contact to update
     * @param name The new name (or null if unchanged)
     * @param company The new company (or null if unchanged)
     * @param phoneNumber The new phone number (or null if unchanged)
     * @param email The new email (or null if unchanged)
     * @return True if update was successful, false otherwise
     */
    suspend fun update(
        id: Int,
        name: String? = null,
        company: String? = null,
        phoneNumber: String? = null,
        email: String? = null
    ): Boolean

    /**
     * Delete a contact by ID
     * @param id The ID of the contact to delete
     * @return True if deletion was successful, false otherwise
     */
    suspend fun delete(id: Int): Boolean
}