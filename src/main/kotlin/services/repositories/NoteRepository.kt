package com.example.crm.services.repositories

import com.example.crm.models.Note
import java.util.UUID

interface NoteRepository {

    suspend fun findByUserId(userId: UUID): List<Note>

    suspend fun findByContact(userId: UUID, contactId: Int): List<Note>

    suspend fun findById(id: Int): Note?

    suspend fun create(
        userId: UUID,
        title: String,
        description: String? = null,
        contactIds: List<Int>
    ): Note

    suspend fun update(
        id: Int,
        title: String,
        description: String,
        contactIds: List<Int>
    ): Boolean


    suspend fun delete(id: Int): Boolean
}