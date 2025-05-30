package com.example.crm.models

import com.example.crm.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

data class Contact(
    val id: Int,
    val userId: UUID,
    val name: String,
    val company: String?,
    val phoneNumber: String?,
    val contactEmail: String?,
)
