package com.example.litterboom.data

data class Event(
    val id: Int = 0,
    val name: String,
    val date: Long,
    val location: String,
    val isOpen: Boolean = true
)
