package com.example.crm.routing

import com.example.crm.routing.request.ContactRequest
import com.example.crm.routing.routeconfigs.*
import com.example.crm.services.ContactService
import com.example.crm.services.JwtService
import com.example.crm.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Application.configureRouting(
    userService: UserService,
    jwtService: JwtService,
    contactService: ContactService
) {
    val currentUser = "current-user"
    routing {
        authRoute(jwtService)
        userRoute(userService)
        contactRoute(contactService)
    }
}
