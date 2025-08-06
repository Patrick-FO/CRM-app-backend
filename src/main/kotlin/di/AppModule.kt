package com.example.crm.di

import io.ktor.server.application.Application
import org.koin.dsl.module

fun appModule(application: Application) = module {
    single { application }
}