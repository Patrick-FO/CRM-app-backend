package com.example.crm.models

import com.example.crm.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

data class Note(
    val id: Int,
    val userId: UUID,
    val contactIds: List<Int>,
    val title: String,
    val description: String?
)
