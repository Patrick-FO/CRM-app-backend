package com.example.crm.di

import com.example.crm.config.DatabaseConfig
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module

val databaseModule = module {
    single<DatabaseConfig> {
        DatabaseConfig(application = get())
    }

    single<Database> {
        get<DatabaseConfig>().initDatabase()
    }
}