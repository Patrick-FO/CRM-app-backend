package com.example.crm.routing.routeconfigs

import com.example.crm.routing.request.ContactRequest
import com.example.crm.services.ContactService
import com.example.crm.services.NoteService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import java.util.*
import io.ktor.server.response.*

fun Route.contactRoute(contactService: ContactService, noteService: NoteService) {
    route("/api/users/{userId}/contacts") {
        authenticate("user", "admin") {
            noteRoutes(noteService)

            get {
                val principal = call.principal<JWTPrincipal>()
                //TODO Consider removing line below
                val username = principal?.payload?.getClaim("username")?.asString()
                val authenticatedUserId = principal?.payload?.getClaim("userId")?.asString()
                val role = principal?.payload?.getClaim("role")?.asString()

                val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

                // Check if the authenticated user is accessing their own contacts
                if (role != "admin" && authenticatedUserId != userId.toString()) {
                    return@get call.respond(HttpStatusCode.Forbidden, "You can only access your own contacts")
                }

                val contacts = contactService.findByUserId(userId)
                call.respond(contacts)
            }

            post {
                val principal = call.principal<JWTPrincipal>()
                val authenticatedUserId = principal?.payload?.getClaim("userId")?.asString()
                val role = principal?.payload?.getClaim("role")?.asString()

                val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

                if (role != "admin" && authenticatedUserId != userId.toString()) {
                    return@post call.respond(HttpStatusCode.Forbidden, "You can only create contacts for yourself")
                }

                val request = call.receive<ContactRequest>()
                val contact = contactService.create(userId, request)
                call.respond(HttpStatusCode.Created, contact)
            }

            route("/{contactId}") {
                contactNotesRoute(noteService)

                get {
                    val principal = call.principal<JWTPrincipal>()
                    val authenticatedUserId = principal?.payload?.getClaim("userId")?.asString()
                    val role = principal?.payload?.getClaim("role")?.asString()

                    val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

                    val contactId = call.parameters["contactId"]?.toIntOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid contact ID")

                    if (role != "admin" && authenticatedUserId != userId.toString()) {
                        return@get call.respond(HttpStatusCode.Forbidden, "You can only access your own contacts")
                    }

                    val contact = contactService.findById(contactId, userId)
                        ?: return@get call.respond(HttpStatusCode.NotFound, "Contact not found")

                    call.respond(contact)
                }

                put {
                    val principal = call.principal<JWTPrincipal>()
                    val authenticatedUserId = principal?.payload?.getClaim("userId")?.asString()
                    val role = principal?.payload?.getClaim("role")?.asString()

                    val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                        ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

                    val contactId = call.parameters["contactId"]?.toIntOrNull()
                        ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid contact ID")

                    if (role != "admin" && authenticatedUserId != userId.toString()) {
                        return@put call.respond(HttpStatusCode.Forbidden, "You can only update your own contacts")
                    }

                    val request = call.receive<ContactRequest>()
                    val success = contactService.update(contactId, userId, request)

                    if (success) {
                        call.respond(HttpStatusCode.OK, "Contact updated successfully")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Contact not found")
                    }
                }

                delete {
                    val principal = call.principal<JWTPrincipal>()
                    val authenticatedUserId = principal?.payload?.getClaim("userId")?.asString()
                    val role = principal?.payload?.getClaim("role")?.asString()

                    val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                        ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

                    val contactId = call.parameters["contactId"]?.toIntOrNull()
                        ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid contact ID")

                    if (role != "admin" && authenticatedUserId != userId.toString()) {
                        return@delete call.respond(HttpStatusCode.Forbidden, "You can only delete your own contacts")
                    }

                    val success = contactService.delete(contactId, userId)

                    if (success) {
                        call.respond(HttpStatusCode.OK, "Contact deleted successfully")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Contact not found")
                    }
                }
            }
        }
    }
}