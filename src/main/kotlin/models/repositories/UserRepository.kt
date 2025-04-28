package com.example.crm.models.repositories

import com.example.crm.models.classes.User

interface UserRepository {
    /**
     * Find a user by their username
     * @param username The username to search for
     * @return The user if found, null otherwise
     */
    suspend fun findByUsername(username: String): User?

    /**
     * Find a user by their ID
     * @param id The user ID to search for
     * @return The user if found, null otherwise
     */
    suspend fun findById(id: Int): User?

    /**
     * Create a new user
     * @param username The username for the new user
     * @param email The email for the new user
     * @param passwordHash The hashed password for the new user
     * @return The ID of the newly created user
     */
    suspend fun createUser(username: String, email: String, passwordHash: String): Int

    /**
     * Update a user's information
     * @param id The ID of the user to update
     * @param email The new email (or null if unchanged)
     * @param passwordHash The new password hash (or null if unchanged)
     * @return True if update was successful, false otherwise
     */
    suspend fun updateUser(id: Int, email: String? = null, passwordHash: String? = null): Boolean

    /**
     * Delete a user by their ID
     * @param id The ID of the user to delete
     * @return True if deletion was successful, false otherwise
     */
    suspend fun deleteUser(id: Int): Boolean
}