package com.example.crm.services

import com.example.crm.models.Contact
import com.example.crm.routing.request.ContactRequest
import com.example.crm.routing.response.ContactResponse
import com.example.crm.services.repositories.ContactRepository
import java.util.*

class ContactService(private val contactRepository: ContactRepository) {

    suspend fun findByUserId(userId: UUID): List<ContactResponse> {
        return contactRepository.findByUserId(userId).map { it.toResponse() }
    }

    suspend fun findById(contactId: Int, userId: UUID): ContactResponse? {
        val contact = contactRepository.findById(contactId)
        return if (contact != null && contact.userId == userId) {
            contact.toResponse()
        } else {
            null
        }
    }

    suspend fun create(userId: UUID, request: ContactRequest): ContactResponse {
        val contact = contactRepository.create(
            userId = userId,
            name = request.name,
            company = request.company,
            phoneNumber = request.phoneNumber,
            email = request.contactEmail
        )
        return contact.toResponse()
    }

    suspend fun update(contactId: Int, userId: UUID, request: ContactRequest): Boolean {
        val contact = contactRepository.findById(contactId)
        if (contact != null && contact.userId == userId) {
            return contactRepository.update(
                id = contactId,
                name = request.name,
                company = request.company,
                phoneNumber = request.phoneNumber,
                email = request.contactEmail
            )
        }
        return false
    }

    suspend fun delete(contactId: Int, userId: UUID): Boolean {
        val contact = contactRepository.findById(contactId)
        if (contact != null && contact.userId == userId) {
            return contactRepository.delete(contactId)
        }
        return false
    }
}

fun Contact.toResponse() = ContactResponse(
    id = id,
    userId = userId,
    name = name,
    company = company,
    phoneNumber = phoneNumber,
    contactEmail = contactEmail
)