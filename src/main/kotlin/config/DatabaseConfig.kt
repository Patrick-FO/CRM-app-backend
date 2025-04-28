package com.example.crm.config

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import com.example.crm.models.Users
import com.example.crm.models.Contacts
import com.example.crm.models.Notes

fun Application.configureDatabases() {
    // Connect to PostgreSQL directly
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/crm_db",
        driver = "org.postgresql.Driver",
        user = "patrick",
        password = "qwerty"
    )

    // Verify tables exist
    transaction {
        SchemaUtils.create(Users, Contacts, Notes)
    }
}