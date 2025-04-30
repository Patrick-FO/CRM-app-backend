package com.example.crm.services.repositories

import com.example.crm.models.User
import java.util.*

interface UserRepository {
    fun findAll(): List<User>

    fun findById(id: UUID): User?

    fun findByUsername(username: String): User?

    fun save(user: User): Boolean
}