package com.example.crm.data

import com.example.crm.models.User
import com.example.crm.services.repositories.UserRepository
import java.util.*

class UserRepositoryImpl: UserRepository {
    private val users = mutableListOf<User>()

    override suspend fun findAll(): List<User> = users

    override suspend fun findById(id: UUID): User? =
        users.firstOrNull { it.id == id }

    override suspend fun findByUsername(username: String): User? =
        users.firstOrNull { it.username == username }

    override suspend fun save(user: User): Boolean =
        users.add(user)
}