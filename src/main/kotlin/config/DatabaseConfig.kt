package com.example.crm.config

import org.jetbrains.exposed.sql.Database
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import javax.sql.DataSource

class DatabaseConfig(private val application: Application) {
    fun createDataSource(): DataSource {
        val config = HikariConfig().apply {
            driverClassName = getConfigProperty("database.driver")
            jdbcUrl = getConfigProperty("database.url")
            username = getConfigProperty("database.user")
            password = getConfigProperty("database.password")

            maximumPoolSize = getConfigProperty("database.maxPoolSize").toInt()
            minimumIdle = getConfigProperty("database.minIdle").toIntOrNull() ?: 5

            connectionTestQuery = "SELECT 1"
            connectionTimeout = 30000
            idleTimeout = 600000
            maxLifetime = 1800000

            validate()
        }

        return HikariDataSource(config)
    }

    fun initDatabase(): Database {
        val dataSource = createDataSource()
        return Database.connect(dataSource)
    }

    private fun getConfigProperty(path: String): String =
        application.environment.config.property(path).getString()

    private fun getConfigPropertyOrDefault(path: String, defaultValue: String): String =
        try {
            application.environment.config.property(path).getString()
        } catch (e: Exception) {
            defaultValue
        }
}