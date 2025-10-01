package com.example.litterboom.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "bags",
    foreignKeys = [
        ForeignKey(
            entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Bag(
    @PrimaryKey(autoGenerate = true) val bagId: Int = 0,
    val eventId: Int,
    val bagNumber: Int,
    val weight: Double,
    val isApproved: Boolean = false
)