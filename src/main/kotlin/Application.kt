package com.example.crm

import com.example.crm.config.DatabaseConfig
import com.example.crm.data.ContactRepositoryImpl
import com.example.crm.data.NoteRepositoryImpl
import com.example.crm.plugins.configureHTTP
import com.example.crm.plugins.configureSecurity
import com.example.crm.plugins.configureSerialization
import com.example.crm.data.UserRepositoryImpl
import com.example.crm.models.Contact
import com.example.crm.models.User
import com.example.crm.routing.configureRouting
import com.example.crm.services.ContactService
import com.example.crm.services.JwtService
import com.example.crm.services.NoteService
import com.example.crm.services.UserService
import io.ktor.server.application.*
import java.util.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    DatabaseConfig.init()

    val userRepository = UserRepositoryImpl()
    val userService = UserService(userRepository)
    val jwtService = JwtService(this, userService)
    val contactRepository = ContactRepositoryImpl()
    val contactService = ContactService(contactRepository)
    val noteRepository = NoteRepositoryImpl()
    val noteService = NoteService(noteRepository, contactRepository)

    configureSecurity(jwtService)
    configureSerialization()
    configureHTTP()
    configureRouting(userService, jwtService, contactService, noteService)
}
