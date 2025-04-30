package com.example.crm.services

import com.example.crm.models.classes.User
import com.example.crm.repository.UserRepository
import java.util.*

class UserService(
    private val userRepository: UserRepository
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