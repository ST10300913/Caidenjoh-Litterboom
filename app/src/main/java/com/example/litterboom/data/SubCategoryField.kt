package com.example.litterboom.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "subcategory_fields",
    primaryKeys = ["subCategoryId", "fieldId"],
    foreignKeys = [
        ForeignKey(
            entity = WasteSubCategory::class,
            parentColumns = ["id"],
            childColumns = ["subCategoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LoggingField::class,
            parentColumns = ["id"],
            childColumns = ["fieldId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SubCategoryField(
    val subCategoryId: Int,
    val fieldId: Int
)