package com.example.crm.routing

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val currentUser = "current-user"
    routing {
        post("/login") {

        }

        post("/register") {

        }

        authenticate {
            route("/users/{userId}") {
                get("/contacts") {
                    val userId = call.parameters["userId"]

                    //TODO fetch all the contacts to view in a list later
                }

                get("/contacts/{contactId}") {
                    val userId = call.parameters["userId"]
                    val contactId = call.parameters["contactId"]

                    //TODO select one contact
                }

                post("/contacts") {
                    val userId = call.parameters["userId"]

                    //TODO create a contact
                }

                get("/contacts/{contactId}/notes") {
                    val userId = call.parameters["userId"]
                    val contactId = call.parameters["contactId"]

                    //TODO fetch all the contacts for one contact
                }

                get("/notes") {
                    val userId = call.parameters["userId"]
                }

                post("/notes") {
                    val userId = call.parameters["userId"]
                }
            }
        }
    }
}
