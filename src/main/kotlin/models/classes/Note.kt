package com.example.crm.models.classes

data class Note(
    val userId: Int,
    val contactIds: List<Int>,
    val title: String,
    val description: String
)
