package com.example.crm.controllers

import com.example.crm.models.User
import com.example.crm.routing.request.UserRequest
import com.example.crm.routing.response.UserResponse
import com.example.crm.services.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import java.util.UUID

class UserController(
    private val userService: UserService
) {
    suspend fun createUser(call: ApplicationCall) {
        try {
            val userRequest = call.receive<UserRequest>()

            val createdUser = userService.save(
                user = userRequest.toModel()
            )

            if (createdUser != null) {
                call.response.header("id", createdUser.id.toString())
                call.respond(HttpStatusCode.Created)
            } else {
                call.respond(HttpStatusCode.BadRequest, "User already exists")
            }

        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Invalid request format")
        }
    }

    suspend fun getAllUsers(call: ApplicationCall) {
        try {
            val users = userService.findAll()
            call.respond(users.map { it.toResponse() })
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to fetch users")
        }
    }

    suspend fun getUserById(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]
                ?: return call.respond(HttpStatusCode.BadRequest, "Missing user ID")

            val foundUser = userService.findById(id)
                ?: return call.respond(HttpStatusCode.NotFound, "User not found")

            val principalUsername = extractPrincipalUsername(call)
            if (foundUser.username != principalUsername) {
                return call.respond(HttpStatusCode.NotFound, "User not found")
            }

            call.respond(foundUser.toResponse())

        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to fetch user")
        }
    }

    private fun extractPrincipalUsername(call: ApplicationCall): String? =
        call.principal<JWTPrincipal>()
            ?.payload
            ?.getClaim("username")
            ?.asString()

    private fun User.toResponse(): UserResponse =
        UserResponse(
            id = this.id,
            username = this.username,
        )

    private fun UserRequest.toModel(): User =
        User(
            id = UUID.randomUUID(),
            username = this.username,
            password = this.password
        )
}