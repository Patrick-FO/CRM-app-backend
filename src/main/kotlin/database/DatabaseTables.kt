package com.example.crm.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val id = uuid("id")
    val username = varchar("username", 255).uniqueIndex()
    val password = varchar("password", 255)

    override val primaryKey = PrimaryKey(id)
}

object Contacts : IntIdTable("contacts") {
    val userId = reference("user_id", Users.id)
    val name = varchar("name", 255)
    val company = varchar("company", 255).nullable()
    val phoneNumber = varchar("phone_number", 50).nullable()
    val contactEmail = varchar("contact_email", 255).nullable()
}

object Notes : IntIdTable("notes") {
    val userId = reference("user_id", Users.id)
    val title = varchar("title", 255)
    val description = text("description").nullable()
    val contactIds = text("contact_ids")
}