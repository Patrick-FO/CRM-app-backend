package com.example.crm.routing.routeconfigs

import com.example.crm.controllers.UserController
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userRoute() {
    val userController by inject<UserController>()

    route("/api/user") {
        post {
            userController.createUser(call)
        }

        authenticate("admin") {
            get {
                userController.getAllUsers(call)
            }

            get("/{id}") {
                userController.getUserById(call)
            }
        }
    }
}