package com.example.crm.controllers

import com.example.crm.routing.request.LoginRequest
import com.example.crm.services.JwtService
import com.example.crm.services.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.*

class AuthController(
    private val jwtService: JwtService,
    private val userService: UserService
) {
    suspend fun login(call: ApplicationCall) {
        try {
            val loginRequest = call.receive<LoginRequest>()

            val token = jwtService.createJwtToken(loginRequest)
            val userId = userService.findByUsername(loginRequest.username)?.id

            call.response.header("userId", userId.toString())

            if(token != null) {
                call.respond(hashMapOf("token" to token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
        } catch(e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Invalid request format")
        }
    }
}