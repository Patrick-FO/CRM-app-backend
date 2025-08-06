package com.example.crm.routing.routeconfigs

import com.example.crm.controllers.ContactController
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.contactRoute() {
    val contactController by inject<ContactController>()

    route("/api/users/{userId}/contacts") {
        authenticate("user", "admin") {
            noteRoutes()

            get {
                contactController.getContactsByUserId(call)
            }

            post {
                contactController.createContact(call)
            }

            route("/{contactId}") {
                contactNotesRoute()

                get {
                    contactController.getContactById(call)
                }

                put {
                    contactController.updateContact(call)
                }

                delete {
                    contactController.deleteContact(call)
                }
            }
        }
    }
}