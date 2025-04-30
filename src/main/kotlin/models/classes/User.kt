package com.example.crm.models.classes

import java.util.*


data class User(
    val id: UUID, //Changed from Int to UUID, noted for later db modification
    val username: String,
    //Used to feature email, not anymore, now it's a simple username and password setup
    val password: String
)
