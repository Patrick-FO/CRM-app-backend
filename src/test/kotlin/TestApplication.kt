package com.example.crm

import com.example.crm.plugins.*
import com.example.crm.routing.configureRouting
import com.example.crm.services.JwtService
import io.ktor.server.application.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.testModule() {
    val testDatabase = Database.connect(
        url = "jdbc:h2:mem:test_db;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=PostgreSQL",
        driver = "org.h2.Driver"
    )

    transaction(testDatabase) {
        SchemaUtils.create(
            com.example.crm.database.Users,
            com.example.crm.database.Contacts,
            com.example.crm.database.Notes
        )
    }

    install(Koin) {
        modules(
            module {
                single { testDatabase }
                single { com.example.crm.data.UserRepositoryImpl(get()) }
                single { com.example.crm.data.ContactRepositoryImpl() }
                single { com.example.crm.data.NoteRepositoryImpl() }
                single { com.example.crm.services.UserService(get()) }
                single { com.example.crm.services.ContactService(get()) }
                single { com.example.crm.services.NoteService(get(), get()) }
                single { JwtService(this@testModule, get()) }
                single { com.example.crm.controllers.UserController(get()) }
                single { com.example.crm.controllers.AuthController(get(), get()) }
                single { com.example.crm.controllers.ContactController(get()) }
                single { com.example.crm.controllers.NoteController(get()) }
            }
        )
    }

    configureSerialization()
    configureSecurity()
    configureRouting()
}

fun testApplication(test: suspend ApplicationTestBuilder.() -> Unit) {
    io.ktor.server.testing.testApplication {
        application {
            testModule()
        }
        test()
    }
}