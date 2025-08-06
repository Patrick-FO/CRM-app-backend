package com.example.crm.models

import java.util.UUID

data class Contact(
    val id: Int,
    val userId: UUID,
    val name: String,
    val company: String?,
    val phoneNumber: String?,
    val contactEmail: String?,
)
