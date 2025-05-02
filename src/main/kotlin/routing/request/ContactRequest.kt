package com.example.crm.routing.request

import kotlinx.serialization.Serializable

@Serializable
data class ContactRequest(
    val name: String,
    val company: String? = null,
    val phoneNumber: String? = null,
    val contactEmail: String? = null
)