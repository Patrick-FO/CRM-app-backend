package com.example.crm.data

import com.example.crm.models.Note
import com.example.crm.database.Notes
import com.example.crm.services.repositories.NoteRepository
import com.example.crm.utils.toIntList
import com.example.crm.utils.toJson
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.*


class NoteRepositoryImpl : NoteRepository {

    override suspend fun findByUserId(userId: UUID): List<Note> = transaction {
        Notes.selectAll().where { Notes.userId eq userId }
            .map { row ->
                Note(
                    id = row[Notes.id].value,
                    userId = row[Notes.userId],
                    title = row[Notes.title],
                    description = row[Notes.description],
                    contactIds = row[Notes.contactIds]
                )
            }
    }

    override suspend fun findByContact(userId: UUID, contactId: Int): List<Note> = transaction {
        Notes.selectAll().where { Notes.userId eq userId }
            .map { row ->
                Note(
                    id = row[Notes.id].value,
                    userId = row[Notes.userId],
                    title = row[Notes.title],
                    description = row[Notes.description],
                    contactIds = row[Notes.contactIds]
                )
            }
            .filter { note -> note.contactIds.contains(contactId) }
    }

    override suspend fun findById(id: Int): Note? = transaction {
        Notes.selectAll().where { Notes.id eq id }
            .map { row ->
                Note(
                    id = row[Notes.id].value,
                    userId = row[Notes.userId],
                    title = row[Notes.title],
                    description = row[Notes.description],
                    contactIds = row[Notes.contactIds]
                )
            }
            .singleOrNull()
    }

    override suspend fun create(userId: UUID, title: String, description: String?, contactIds: List<Int>): Note = transaction {
        val insertedId = Notes.insert {
            it[Notes.userId] = userId
            it[Notes.title] = title
            it[Notes.description] = description
            it[Notes.contactIds] = contactIds
        } get Notes.id

        Note(
            id = insertedId.value,
            userId = userId,
            title = title,
            description = description,
            contactIds = contactIds
        )
    }

    override suspend fun update(id: Int, title: String, description: String, contactIds: List<Int>): Boolean = transaction {
        val updatedRows = Notes.update({ Notes.id eq id }) {
            it[Notes.title] = title
            it[Notes.description] = description
            it[Notes.contactIds] = contactIds
        }
        updatedRows > 0
    }

    override suspend fun delete(id: Int): Boolean = transaction {
        val deletedRows = Notes.deleteWhere { Notes.id eq id }
        deletedRows > 0
    }
}