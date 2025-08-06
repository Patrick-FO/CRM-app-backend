package com.example.crm.routing.routeconfigs

import com.example.crm.controllers.AuthController
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.authRoute() {
    val authController by inject<AuthController>()

    route("/api/auth") {
        post {
            authController.login(call)
        }
    }
}