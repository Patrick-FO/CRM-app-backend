package com.example.crm

import com.example.crm.config.configureDatabases
import com.example.crm.routing.configureRouting
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureDatabases()
    configureHTTP()
    configureRouting()
}