package com.example.crm.controllers

import com.example.crm.routing.request.ContactRequest
import com.example.crm.services.ContactService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import java.util.UUID

class ContactController(
    private val contactService: ContactService,
) {
    suspend fun getContactsByUserId(call: ApplicationCall) {
        try {
            val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                ?: return call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

            if (!isAuthorizedForUser(call, userId)) {
                return call.respond(HttpStatusCode.Forbidden, "You can only access your own contacts")
            }

            val contacts = contactService.findByUserId(userId)
            call.respond(contacts)

        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, "Invalid user ID format")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to fetch contacts")
        }
    }

    suspend fun createContact(call: ApplicationCall) {
        try {
            val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                ?: return call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

            if (!isAuthorizedForUser(call, userId)) {
                return call.respond(HttpStatusCode.Forbidden, "You can only create contacts for yourself")
            }

            val request = call.receive<ContactRequest>()
            val contact = contactService.create(userId, request)
            call.respond(HttpStatusCode.Created, contact)

        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, "Invalid user ID format")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Invalid request format")
        }
    }

    suspend fun getContactById(call: ApplicationCall) {
        try {
            val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                ?: return call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

            val contactId = call.parameters["contactId"]?.toIntOrNull()
                ?: return call.respond(HttpStatusCode.BadRequest, "Invalid contact ID")

            if (!isAuthorizedForUser(call, userId)) {
                return call.respond(HttpStatusCode.Forbidden, "You can only access your own contacts")
            }

            val contact = contactService.findById(contactId, userId)
                ?: return call.respond(HttpStatusCode.NotFound, "Contact not found")

            call.respond(contact)

        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to fetch contact")
        }
    }

    suspend fun updateContact(call: ApplicationCall) {
        try {
            val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                ?: return call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

            val contactId = call.parameters["contactId"]?.toIntOrNull()
                ?: return call.respond(HttpStatusCode.BadRequest, "Invalid contact ID")

            if (!isAuthorizedForUser(call, userId)) {
                return call.respond(HttpStatusCode.Forbidden, "You can only update your own contacts")
            }

            val request = call.receive<ContactRequest>()
            val success = contactService.update(contactId, userId, request)

            if (success) {
                call.respond(HttpStatusCode.OK, "Contact updated successfully")
            } else {
                call.respond(HttpStatusCode.NotFound, "Contact not found")
            }

        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Invalid request format")
        }
    }

    suspend fun deleteContact(call: ApplicationCall) {
        try {
            val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                ?: return call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

            val contactId = call.parameters["contactId"]?.toIntOrNull()
                ?: return call.respond(HttpStatusCode.BadRequest, "Invalid contact ID")

            if (!isAuthorizedForUser(call, userId)) {
                return call.respond(HttpStatusCode.Forbidden, "You can only delete your own contacts")
            }

            val success = contactService.delete(contactId, userId)

            if (success) {
                call.respond(HttpStatusCode.OK, "Contact deleted successfully")
            } else {
                call.respond(HttpStatusCode.NotFound, "Contact not found")
            }

        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to delete contact")
        }
    }

    private fun isAuthorizedForUser(call: ApplicationCall, userId: UUID): Boolean {
        val principal = call.principal<JWTPrincipal>()
        val authenticatedUserId = principal?.payload?.getClaim("userId")?.asString()
        val role = principal?.payload?.getClaim("role")?.asString()

        return role == "admin" || authenticatedUserId == userId.toString()
    }
}