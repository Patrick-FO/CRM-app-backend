package com.example.crm.routing.routeconfigs

import com.example.crm.controllers.NoteController
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.noteRoutes() {
    val noteController by inject<NoteController>()

    route("/notes") {
        get {
            noteController.getNotesByUserId(call)
        }

        post {
            noteController.createNote(call)
        }

        route("/{noteId}") {
            get {
                noteController.getNoteById(call)
            }

            put {
                noteController.updateNote(call)
            }

            delete {
                noteController.deleteNote(call)
            }
        }
    }
}

fun Route.contactNotesRoute() {
    val noteController by inject<NoteController>()

    route("/notes") {
        get {
            noteController.getNotesByContactId(call)
        }
    }
}