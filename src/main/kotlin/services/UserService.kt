package com.example.crm.services

import com.example.crm.models.User
import com.example.crm.data.UserRepositoryImpl
import org.mindrot.jbcrypt.BCrypt
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

        val hashedPasswordUser = User(
            id = user.id,
            username = user.username,
            password = hashPassword(user.password)
        )

        return if (foundUser != null) {
            null
        } else {
            userRepository.save(hashedPasswordUser)
            user
        }
    }

    private fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    private fun checkPassword(password: String, hashed: String): Boolean {
        return BCrypt.checkpw(password, hashed)
    }
}