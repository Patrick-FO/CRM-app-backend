package com.example.crm.services.repositories

import com.example.crm.models.Contact

/**
 * Interface for contact data operations
 */
interface ContactRepository {
    suspend fun findByUserId(userId: Int): List<Contact>

    suspend fun findById(id: Int): Contact?

    suspend fun create(
        userId: Int,
        name: String,
        company: String? = null,
        phoneNumber: String? = null,
        email: String? = null
    ): Contact

    suspend fun update(
        id: Int,
        name: String? = null,
        company: String? = null,
        phoneNumber: String? = null,
        email: String? = null
    ): Boolean

    suspend fun delete(id: Int): Boolean
}