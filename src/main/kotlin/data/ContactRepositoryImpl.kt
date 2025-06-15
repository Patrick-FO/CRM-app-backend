package com.example.crm.data

import com.example.crm.models.Contact
import com.example.crm.database.Contacts
import com.example.crm.services.repositories.ContactRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.*

class ContactRepositoryImpl : ContactRepository {

    override suspend fun findByUserId(userId: UUID): List<Contact> = transaction {
        Contacts.selectAll().where { Contacts.userId eq userId }
            .map { row ->
                Contact(
                    id = row[Contacts.id].value,
                    userId = row[Contacts.userId],
                    name = row[Contacts.name],
                    company = row[Contacts.company],
                    phoneNumber = row[Contacts.phoneNumber],
                    contactEmail = row[Contacts.contactEmail]
                )
            }
    }

    override suspend fun findById(id: Int): Contact? = transaction {
        Contacts.selectAll().where { Contacts.id eq id }
            .map { row ->
                Contact(
                    id = row[Contacts.id].value,
                    userId = row[Contacts.userId],
                    name = row[Contacts.name],
                    company = row[Contacts.company],
                    phoneNumber = row[Contacts.phoneNumber],
                    contactEmail = row[Contacts.contactEmail]
                )
            }
            .singleOrNull()
    }

    override suspend fun create(
        userId: UUID,
        name: String,
        company: String?,
        phoneNumber: String?,
        email: String?
    ): Contact = transaction {
        val insertedId = Contacts.insert {
            it[Contacts.userId] = userId
            it[Contacts.name] = name
            it[Contacts.company] = company
            it[Contacts.phoneNumber] = phoneNumber
            it[Contacts.contactEmail] = email
        } get Contacts.id

        Contact(
            id = insertedId.value,
            userId = userId,
            name = name,
            company = company,
            phoneNumber = phoneNumber,
            contactEmail = email
        )
    }

    override suspend fun update(
        id: Int,
        name: String,
        company: String?,
        phoneNumber: String?,
        email: String?
    ): Boolean = transaction {
        val updatedRows = Contacts.update({ Contacts.id eq id }) {
            it[Contacts.name] = name
            it[Contacts.company] = company
            it[Contacts.phoneNumber] = phoneNumber
            it[Contacts.contactEmail] = email
        }
        updatedRows > 0
    }

    override suspend fun delete(id: Int): Boolean = transaction {
        val deletedRows = Contacts.deleteWhere { Contacts.id eq id }
        deletedRows > 0
    }
}