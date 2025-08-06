package com.example.crm.di

import com.example.crm.controllers.AuthController
import com.example.crm.controllers.ContactController
import com.example.crm.controllers.NoteController
import com.example.crm.controllers.UserController
import org.koin.dsl.module

val controllerModule = module {
    single<AuthController> {
        AuthController(
            jwtService = get(),
            userService = get()
        )
    }

    single<UserController> {
        UserController(
            userService = get()
        )
    }

    single<ContactController> {
        ContactController(
            contactService = get()
        )
    }

    single<NoteController> {
        NoteController(
            noteService = get()
        )
    }
}