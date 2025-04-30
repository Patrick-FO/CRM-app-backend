package com.example.crm.models


data class Note(
    val id: Int,
    val userId: Int,
    val contactIds: List<Int>,
    val title: String,
    val description: String?
)
