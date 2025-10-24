package com.example.litterboom.data

data class WasteSubCategory(
    val id: Int = 0,
    val name: String,
    val categoryId: Int,
    val isActive: Boolean = true
)
