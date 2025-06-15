package com.example.crm.utils

import kotlinx.serialization.json.Json

fun List<Int>.toJson(): String = Json.encodeToString(this)

fun String.toIntList(): List<Int> = try {
    Json.decodeFromString<List<Int>>(this)
} catch (e: Exception) {
    emptyList()
}