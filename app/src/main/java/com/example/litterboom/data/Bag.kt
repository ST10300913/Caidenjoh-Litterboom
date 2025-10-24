package com.example.litterboom.data

data class Bag(
    val bagId: Int = 0,
    val eventId: Int,
    val bagNumber: Int,
    val weight: Double,
    val isApproved: Boolean = false
)
