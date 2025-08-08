package com.example.crm.routing

import com.example.crm.routing.routeconfigs.*
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authRoute()
        userRoute()
        contactRoute()
        staticResources("/docs", "docs")
    }
}
