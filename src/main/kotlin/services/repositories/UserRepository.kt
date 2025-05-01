package com.example.crm.services.repositories

import com.example.crm.models.User
import java.util.*

interface UserRepository {
    suspend fun findAll(): List<User>

    suspend fun findById(id: UUID): User?

    suspend fun findByUsername(username: String): User?

    suspend fun save(user: User): Boolean
}