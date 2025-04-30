package com.example.crm.models


data class Contact(
    val id: Int,
    val userId: Int,
    val name: String,
    val company: String?,
    val phoneNumber: String?,
    val contactEmail: String?,
)
