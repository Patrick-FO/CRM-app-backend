package com.example.crm.data

import com.example.crm.models.Note
import com.example.crm.services.repositories.NoteRepository
import java.util.*


class NoteRepositoryImpl: NoteRepository {
    private val notes = mutableListOf<Note>()
    private var nextId = 1

    override suspend fun findByUserId(userId: UUID): List<Note> {
        return notes.filter { it.userId == userId }
    }

    override suspend fun findByContact(userId: UUID, contactId: Int): List<Note> {
        return notes.filter { it.userId == userId && it.contactIds.any { contactIdFromList -> contactIdFromList == contactId } }
    }

    override suspend fun findById(id: Int): Note? {
        return notes.find { it.id == id }
    }

    override suspend fun create(userId: UUID, title: String, description: String?, contactIds: List<Int>): Note {
        val note = Note(
            id = nextId++,
            userId = userId,
            contactIds = contactIds,
            title = title,
            description = description,
        )
        notes.add(note)
        return note
    }

    override suspend fun update(id: Int, title: String, description: String, contactIds: List<Int>): Boolean {
        var note = notes.find { it.id == id } ?: return false

        note = note.copy(
            title = title,
            description = description,
            contactIds = contactIds
        )

        val index = notes.indexOfFirst { it.id == id }
        if (index != -1) {
            notes[index] = note
            return true
        }
        return false
    }

    override suspend fun delete(id: Int): Boolean {
        return notes.removeIf { it.id == id }
    }

}