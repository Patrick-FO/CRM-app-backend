package com.example.crm.services

import com.example.crm.models.User
import com.example.crm.data.UserRepositoryImpl
import java.util.*

class UserService(
    private val userRepository: UserRepositoryImpl
) {
    fun findAll(): List<User> =
        userRepository.findAll()

    fun findById(id: String): User? =
        userRepository.findById(
            id = UUID.fromString(id)
        )

    fun findByUsername(username: String): User? =
        userRepository.findByUsername(username)

    fun save(user: User): User? {
        val foundUser = findByUsername(user.username)
        return if (foundUser != null) {
            null
        } else {
            userRepository.save(user)
            user
        }
    }
}