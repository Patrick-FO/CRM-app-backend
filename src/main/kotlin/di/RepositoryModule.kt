package com.example.crm.di

import com.example.crm.data.ContactRepositoryImpl
import com.example.crm.data.NoteRepositoryImpl
import org.koin.dsl.module
import com.example.crm.services.repositories.UserRepository
import com.example.crm.data.UserRepositoryImpl
import com.example.crm.services.repositories.ContactRepository
import com.example.crm.services.repositories.NoteRepository

val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl(database = get()) }
    single<ContactRepository> { ContactRepositoryImpl() }
    single<NoteRepository> { NoteRepositoryImpl() }
}