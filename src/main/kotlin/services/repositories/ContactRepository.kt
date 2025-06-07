package com.example.crm.services.repositories

import com.example.crm.models.Contact
import java.util.UUID

interface ContactRepository {
    suspend fun findByUserId(userId: UUID): List<Contact>

    suspend fun findById(id: Int): Contact?

    suspend fun create(
        userId: UUID,
        name: String,
        company: String? = null,
        phoneNumber: String? = null,
        email: String? = null
    ): Contact

    suspend fun update(
        id: Int,
        name: String,
        company: String? = null,
        phoneNumber: String? = null,
        email: String? = null
    ): Boolean

    suspend fun delete(id: Int): Boolean
}