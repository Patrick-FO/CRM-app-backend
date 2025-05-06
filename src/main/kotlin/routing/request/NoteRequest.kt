package com.example.crm.routing.request

import kotlinx.serialization.Serializable

@Serializable
data class NoteRequest(
    val contactIds: List<Int>,
    val title: String,
    val description: String
)