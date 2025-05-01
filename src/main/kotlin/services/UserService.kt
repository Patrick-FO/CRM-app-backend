package com.example.crm.services

import com.example.crm.models.User
import com.example.crm.data.UserRepositoryImpl
import java.util.*

class UserService(
    private val userRepository: UserRepositoryImpl
) {
    suspend fun findAll(): List<User> =
        userRepository.findAll()

    suspend fun findById(id: String): User? =
        userRepository.findById(
            id = UUID.fromString(id)
        )

    suspend fun findByUsername(username: String): User? =
        userRepository.findByUsername(username)

    suspend fun save(user: User): User? {
        val foundUser = findByUsername(user.username)
        return if (foundUser != null) {
            null
        } else {
            userRepository.save(user)
            user
        }
    }
}