package com.example.crm.data

import com.example.crm.models.User
import com.example.crm.database.Users
import com.example.crm.services.repositories.UserRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class UserRepositoryImpl : UserRepository {

    override suspend fun findAll(): List<User> = transaction {
        Users.selectAll().map { row ->
            User(
                id = row[Users.id],
                username = row[Users.username],
                password = row[Users.password]
            )
        }
    }

    override suspend fun findById(id: UUID): User? = transaction {
        Users.selectAll().where { Users.id eq id }
            .map { row ->
                User(
                    id = row[Users.id],
                    username = row[Users.username],
                    password = row[Users.password]
                )
            }
            .singleOrNull()
    }

    override suspend fun findByUsername(username: String): User? = transaction {
        Users.selectAll().where { Users.username eq username }
            .map { row ->
                User(
                    id = row[Users.id],
                    username = row[Users.username],
                    password = row[Users.password]
                )
            }
            .singleOrNull()
    }

    override suspend fun save(user: User): Boolean = transaction {
        try {
            Users.insert {
                it[Users.id] = user.id
                it[Users.username] = user.username
                it[Users.password] = user.password
            }
            true
        } catch (e: Exception) {
            println("Error saving user: ${e.message}")
            false
        }
    }
}