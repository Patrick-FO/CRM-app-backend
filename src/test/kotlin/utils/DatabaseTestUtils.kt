package com.example.crm.utils

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseTestUtils {
    fun createInMemoryDatabase(): Database {
        return Database.connect(
            url = "jdbc:h2:mem:test_db_${System.currentTimeMillis()};DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=PostgreSQL",
            driver = "org.h2.Driver"
        )
    }

    fun clearAllTables(database: Database) {
        transaction(database) {
            SchemaUtils.drop(
                com.example.crm.database.Notes,
                com.example.crm.database.Contacts,
                com.example.crm.database.Users
            )
            SchemaUtils.create(
                com.example.crm.database.Users,
                com.example.crm.database.Contacts,
                com.example.crm.database.Notes
            )
        }
    }

    fun getRecordCounts(database: Database): Map<String, Int> {
        return transaction(database) {
            mapOf(
                "users" to com.example.crm.database.Users.selectAll().count().toInt(),
                "contacts" to com.example.crm.database.Contacts.selectAll().count().toInt(),
                "notes" to com.example.crm.database.Notes.selectAll().count().toInt()
            )
        }
    }
}