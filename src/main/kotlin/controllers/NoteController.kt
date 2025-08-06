package com.example.crm.controllers

import com.example.crm.routing.request.NoteRequest
import com.example.crm.services.NoteService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import java.util.UUID

class NoteController(
    private val noteService: NoteService
) {
    suspend fun getNotesByUserId(call: ApplicationCall) {
        try {
            val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                ?: return call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

            if (!isAuthorizedForUser(call, userId)) {
                return call.respond(HttpStatusCode.Forbidden, "You can only access your own notes")
            }

            val notes = noteService.findByUserId(userId)
            call.respond(HttpStatusCode.OK, notes)

        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, "Invalid user ID format")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to fetch notes")
        }
    }

    suspend fun createNote(call: ApplicationCall) {
        try {
            val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                ?: return call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

            if (!isAuthorizedForUser(call, userId)) {
                return call.respond(HttpStatusCode.Forbidden, "You can only create notes for yourself")
            }

            val request = call.receive<NoteRequest>()
            val note = noteService.create(userId, request)

            call.respond(HttpStatusCode.Created, note)

        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, "Invalid user ID format")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Invalid request format")
        }
    }

    suspend fun getNoteById(call: ApplicationCall) {
        try {
            val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                ?: return call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

            val noteId = call.parameters["noteId"]?.toIntOrNull()
                ?: return call.respond(HttpStatusCode.BadRequest, "Invalid note ID")

            if (!isAuthorizedForUser(call, userId)) {
                return call.respond(HttpStatusCode.Forbidden, "You can only access your own notes")
            }

            val note = noteService.findById(noteId, userId)
                ?: return call.respond(HttpStatusCode.NotFound, "Note not found")

            call.respond(note)

        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to fetch note")
        }
    }

    suspend fun updateNote(call: ApplicationCall) {
        try {
            val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                ?: return call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

            val noteId = call.parameters["noteId"]?.toIntOrNull()
                ?: return call.respond(HttpStatusCode.BadRequest, "Invalid note ID")

            if (!isAuthorizedForUser(call, userId)) {
                return call.respond(HttpStatusCode.Forbidden, "You can only update your own notes")
            }

            val request = call.receive<NoteRequest>()
            val success = noteService.update(noteId, userId, request)

            if (success) {
                call.respond(HttpStatusCode.OK, "Note updated successfully")
            } else {
                call.respond(HttpStatusCode.NotFound, "Note not found")
            }

        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Invalid request format")
        }
    }

    suspend fun deleteNote(call: ApplicationCall) {
        try {
            val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                ?: return call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

            val noteId = call.parameters["noteId"]?.toIntOrNull()
                ?: return call.respond(HttpStatusCode.BadRequest, "Invalid note ID")

            if (!isAuthorizedForUser(call, userId)) {
                return call.respond(HttpStatusCode.Forbidden, "You can only delete your own notes")
            }

            val success = noteService.delete(noteId, userId)

            if (success) {
                call.respond(HttpStatusCode.OK, "Note deleted successfully")
            } else {
                call.respond(HttpStatusCode.NotFound, "Note not found")
            }

        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to delete note")
        }
    }

    suspend fun getNotesByContactId(call: ApplicationCall) {
        try {
            val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                ?: return call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

            val contactId = call.parameters["contactId"]?.toIntOrNull()
                ?: return call.respond(HttpStatusCode.BadRequest, "Invalid contact ID")

            if (!isAuthorizedForUser(call, userId)) {
                return call.respond(HttpStatusCode.Forbidden, "You can only access your own notes")
            }

            val notes = noteService.findByContact(userId, contactId)
            call.respond(HttpStatusCode.OK, notes)
        } catch(e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
        } catch(e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Couldn't get notes for contact")
        }
    }

    private fun isAuthorizedForUser(call: ApplicationCall, userId: UUID): Boolean {
        val principal = call.principal<JWTPrincipal>()
        val authenticatedUserId = principal?.payload?.getClaim("userId")?.asString()
        val role = principal?.payload?.getClaim("role")?.asString()

        return role == "admin" || authenticatedUserId == userId.toString()
    }
}