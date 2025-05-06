package com.example.crm.services

import com.example.crm.models.Note
import com.example.crm.routing.request.NoteRequest
import com.example.crm.routing.response.NoteResponse
import com.example.crm.services.repositories.ContactRepository
import com.example.crm.services.repositories.NoteRepository
import java.util.*

class NoteService(
    private val noteRepository: NoteRepository,
    private val contactRepository: ContactRepository
) {
    suspend fun findByUserId(userId: UUID): List<NoteResponse> {
        return noteRepository.findByUserId(userId).map { it.toResponse() }
    }

    suspend fun findByContact(userId: UUID, contactId: Int): List<NoteResponse> {
        val contact = contactRepository.findById(contactId)

        if(contact == null || contact.userId != userId) {
            return emptyList()
        }

        return noteRepository.findByContact(userId, contactId).map { it.toResponse() }
    }

    suspend fun findById(id: Int, userId: UUID): NoteResponse? {
        val note = noteRepository.findById(id)

        return if(note != null && note.userId == userId) {
            note.toResponse()
        } else {
            null
        }
    }

    suspend fun create(userId: UUID, noteRequest: NoteRequest): NoteResponse {
        val validContactIds = noteRequest.contactIds.filter { contactId ->
            contactRepository.findById(contactId)?.userId == userId
        }

        val note = noteRepository.create(
            userId = userId,
            title = noteRequest.title,
            description = noteRequest.description,
            contactIds = validContactIds
        )

        return note.toResponse()
    }

    suspend fun update(id: Int, userId: UUID, noteRequest: NoteRequest): Boolean {
        val note = noteRepository.findById(id)
        if(note != null && note.userId == userId) {
            return noteRepository.update(
                id = id,
                title = noteRequest.title,
                description = noteRequest.description,
                contactIds = noteRequest.contactIds
            )
        }
        return false
    }

    suspend fun delete(id: Int, userId: UUID): Boolean {
        val note = noteRepository.findById(id)
        if(note != null && note.userId == userId) {
            return noteRepository.delete(id)
        }
        return false
    }
}

fun Note.toResponse() = NoteResponse(
    id = id,
    userId = userId,
    contactIds = contactIds,
    title = title,
    description = description,
)