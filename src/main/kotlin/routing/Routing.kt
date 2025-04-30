package com.example.crm.routing

import com.example.crm.models.User
import com.example.crm.services.JwtService
import com.example.crm.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userService: UserService,
    jwtService: JwtService
) {
    val currentUser = "current-user"
    routing {
        route("/api/auth") {
            authRoute(jwtService)
        }

        route("/api/user") {
            userRoute(userService)
        }

        authenticate {
            get {
                val users = userService.findAll()

                call.respond(message = users.map(User::toResponse))
            }
        }

        authenticate("another-auth") {
            get("/{id}") {
                val id: String = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)

                val foundUser = userService.findById(id) ?: return@get call.respond(HttpStatusCode.NotFound)


                if(foundUser.username != extractPrincipalUsername(call))
                    return@get call.respond(HttpStatusCode.NotFound)

                call.respond(
                    message = foundUser.toResponse()
                )
            }
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
