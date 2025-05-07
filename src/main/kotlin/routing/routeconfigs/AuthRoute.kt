package com.example.crm.routing.routeconfigs

import com.example.crm.routing.request.LoginRequest
import com.example.crm.services.JwtService
import com.example.crm.services.UserService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoute(jwtService: JwtService, userService: UserService) {
    route("/api/auth") {
        post {
            val loginRequest = call.receive<LoginRequest>()

            val token = jwtService.createJwtToken(loginRequest)
            val userId = userService.findByUsername(loginRequest.username)?.id

            call.response.header("userId", userId.toString())
            token?.let {
                call.respond(hashMapOf("token" to it))
            } ?: call.respond(HttpStatusCode.Unauthorized)
        }
    }
}