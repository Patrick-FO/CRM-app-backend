package com.example.crm.routing.response

import com.example.crm.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class NoteResponse(
    val id: Int,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    val contactIds: List<Int>,
    val title: String,
    val description: String?
)