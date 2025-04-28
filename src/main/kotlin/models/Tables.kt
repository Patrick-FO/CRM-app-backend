package com.example.crm.models

import org.jetbrains.exposed.sql.Table

// Define table objects
object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 128)

    override val primaryKey = PrimaryKey(id)
}

object Contacts : Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id)
    val name = varchar("name", 100)
    val company = varchar("company", 100).nullable()
    val phoneNumber = varchar("phone_number", 20).nullable()
    val email = varchar("email", 100).nullable()

    override val primaryKey = PrimaryKey(id)
}

object Notes : Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id)
    val title = varchar("title", 100)
    val description = text("description")
    val contactIds = array<Int>("contact_ids")

    override val primaryKey = PrimaryKey(id)
}