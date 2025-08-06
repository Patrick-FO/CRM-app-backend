package com.example.crm.di

import com.example.crm.services.ContactService
import com.example.crm.services.JwtService
import com.example.crm.services.NoteService
import com.example.crm.services.UserService
import org.koin.dsl.module

val serviceModule = module {
    single<JwtService> {
        JwtService(
            userService = get(),
            application = get()
        )
    }

    single<UserService> {
        UserService(
            userRepository = get()
        )
    }

    single<ContactService> {
        ContactService(
            contactRepository = get()
        )
    }

    single<NoteService> {
        NoteService(
            noteRepository = get(),
            contactRepository = get()
        )
    }
}