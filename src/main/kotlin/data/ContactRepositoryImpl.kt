package com.example.crm.data

import com.example.crm.models.Contact
import com.example.crm.services.repositories.ContactRepository
import java.util.*

class ContactRepositoryImpl(): ContactRepository {
    private val contacts = mutableListOf<Contact>()
    private var nextId = 1

    override suspend fun findByUserId(userId: UUID): List<Contact> {
        return contacts.filter { it.userId == userId }
    }

    override suspend fun findById(id: Int): Contact? {
        return contacts.find { it.id == id }
    }

    override suspend fun create(
        userId: UUID,
        name: String,
        company: String?,
        phoneNumber: String?,
        email: String?
    ): Contact {
        val contact = Contact(
            id = nextId++,
            userId = userId,
            name = name,
            company = company,
            phoneNumber = phoneNumber,
            contactEmail = email
        )
        contacts.add(contact)
        return contact
    }

    // Fix: Update method that allows setting fields to null
    override suspend fun update(
        id: Int,
        name: String,      // Name is required, so not nullable
        company: String?,  // These can be set to null
        phoneNumber: String?,
        email: String?
    ): Boolean {
        val contact = contacts.find { it.id == id } ?: return false

        // Directly assign the values - null means "set to null"
        val updatedContact = contact.copy(
            name = name,
            company = company,
            phoneNumber = phoneNumber,
            contactEmail = email
        )

        val index = contacts.indexOfFirst { it.id == id }
        if (index != -1) {
            contacts[index] = updatedContact
            return true
        }
        return false
    }

    override suspend fun delete(id: Int): Boolean {
        return contacts.removeIf { it.id == id }
    }
}