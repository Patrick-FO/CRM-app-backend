package com.example.crm

import com.example.crm.plugins.configureHTTP
import com.example.crm.plugins.configureSecurity
import com.example.crm.plugins.configureSerialization
import com.example.crm.di.appModule
import com.example.crm.di.controllerModule
import com.example.crm.di.databaseModule
import com.example.crm.di.repositoryModule
import com.example.crm.di.serviceModule
import com.example.crm.routing.configureRouting
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    install(Koin) {
        slf4jLogger()
        modules(
            appModule(this@module),
            databaseModule,
            repositoryModule,
            serviceModule,
            controllerModule,
        )
    }

    configureSecurity()
    configureSerialization()
    configureHTTP()
    configureRouting()
    //TODO Remove in production, sensitive token for CLI admin tool
    //val jwtService by inject<JwtService>()
    //println(jwtService.createAdminToken())
}

