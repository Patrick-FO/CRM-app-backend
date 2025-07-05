package com.example.crm.routing.routeconfigs

import com.example.crm.routing.request.NoteRequest
import com.example.crm.services.NoteService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.noteRoutes(noteService: NoteService) {
    route("/notes") {
        get {
            val principal = call.principal<JWTPrincipal>()
            val authenticatedUserId = principal?.payload?.getClaim("userId")?.asString()

            val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

            if(authenticatedUserId != userId.toString()) {
                return@get call.respond(HttpStatusCode.Forbidden, "You can only access your own notes")
            }

            val notes = noteService.findByUserId(userId)
            call.respond(HttpStatusCode.Found, notes)
        }

        post {
            val principal = call.principal<JWTPrincipal>()
            val authenticatedUserId = principal?.payload?.getClaim("userId")?.asString()

            val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

            if (authenticatedUserId != userId.toString()) {
                return@post call.respond(HttpStatusCode.Forbidden, "You can only create notes for yourself")
            }

            val request = call.receive<NoteRequest>()
            val note = noteService.create(userId, request)

            call.respond(HttpStatusCode.Created, note)
        }

        route("/{noteId}") {
            get {
                //TODO Are we even going to use this endpoint?

                val principal = call.principal<JWTPrincipal>()
                val authenticatedUserId = principal?.payload?.getClaim("userId")?.asString()

                val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

                val noteId = call.parameters["noteId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid note ID")

                if (authenticatedUserId != userId.toString()) {
                    return@get call.respond(HttpStatusCode.Forbidden, "You can only access your own notes")
                }

                val note = noteService.findById(noteId, userId)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Note not found")

                call.respond(note)
            }

            put {
                val principal = call.principal<JWTPrincipal>()
                val authenticatedUserId = principal?.payload?.getClaim("userId")?.asString()

                val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

                val noteId = call.parameters["noteId"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid note ID")

                if (authenticatedUserId != userId.toString()) {
                    return@put call.respond(HttpStatusCode.Forbidden, "You can only access your own notes")
                }

                val request = call.receive<NoteRequest>()
                val success = noteService.update(noteId, userId, request)

                //TODO On the else statement, should we really have NotFound? Maybe bad request instead?
                //TODO Can we make it more comfortable for error handling by adding different status codes for different cases?
                //TODO We need to do the same for the delete endpoint
                if(success) {
                    call.respond(HttpStatusCode.OK, "Note updated successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Note not found")
                }
            }

            delete {
                val principal = call.principal<JWTPrincipal>()
                val authenticatedUserId = principal?.payload?.getClaim("userId")?.asString()

                val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

                val noteId = call.parameters["noteId"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid note ID")

                if (authenticatedUserId != userId.toString()) {
                    return@delete call.respond(HttpStatusCode.Forbidden, "You can only access your own notes")
                }

                val success = noteService.delete(noteId, userId)

                if(success) {
                    call.respond(HttpStatusCode.OK, "Note deleted successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Note not found")
                }
            }
        }
    }
}

fun Route.contactNotesRoute(noteService: NoteService) {
    route("/notes") {
        get {
            val principal = call.principal<JWTPrincipal>()
            val authenticatedUserId = principal?.payload?.getClaim("userId")?.asString()

            val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

            val contactId = call.parameters["contactId"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid contact ID")

            if (authenticatedUserId != userId.toString()) {
                return@get call.respond(HttpStatusCode.Forbidden, "You can only access your own notes")
            }

            //TODO Create nullability, therefore adding safety
            val notes = noteService.findByContact(userId, contactId)
            //?: return@get call.respond(HttpStatusCode.NotFound, "Note not found")

            call.respond(notes)
        }
    }
}