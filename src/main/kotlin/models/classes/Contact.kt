package com.example.crm.models.classes

data class Contact(
    val userId: Int,
    val id: Int,
    val name: String,
    val company: String,
    val phoneNumber: String,
    val contactEmail: String,
)
