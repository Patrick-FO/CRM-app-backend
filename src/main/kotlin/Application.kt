package com.example.crm

import com.example.crm.config.configureDatabases
import com.example.crm.plugins.configureHTTP
import com.example.crm.plugins.configureSecurity
import com.example.crm.plugins.configureSerialization
import com.example.crm.repository.UserRepository
import com.example.crm.routing.configureRouting
import com.example.crm.services.JwtService
import com.example.crm.services.UserService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val userRepository = UserRepository()
    val userService = UserService(userRepository)
    val jwtService = JwtService(this, userService)

    configureSecurity(jwtService)
    configureSerialization()
    //configureDatabases()
    configureHTTP()
    configureRouting(userService, jwtService)
}
