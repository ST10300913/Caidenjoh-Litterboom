package com.example.litterboom.data

data class ItemPhoto(
    val id: Int = 0,
    val subCategoryId: Int,
    val uri: String,           // Will store Firebase Storage download URL
    val takenAt: Long = System.currentTimeMillis()
)
