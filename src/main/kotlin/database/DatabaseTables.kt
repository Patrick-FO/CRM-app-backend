package com.example.crm.database

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.json.json

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
    val contactIds = json<List<Int>>(
        "contact_ids",
        Json.Default,
        ListSerializer(Int.serializer())
    )
}