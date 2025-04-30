package com.example.crm.data

import com.example.crm.models.User
import com.example.crm.services.repositories.UserRepository
import java.util.*

class UserRepositoryImpl: UserRepository {
    private val users = mutableListOf<User>()

    override fun findAll(): List<User> = users

    override fun findById(id: UUID): User? =
        users.firstOrNull { it.id == id }

    override fun findByUsername(username: String): User? =
        users.firstOrNull { it.username == username }

    override fun save(user: User): Boolean =
        users.add(user)
}