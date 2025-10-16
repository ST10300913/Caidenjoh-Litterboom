package com.example.litterboom.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LoggedWasteDao {
    @Insert
    suspend fun insertLoggedWaste(item: LoggedWaste)

    @Query("SELECT * FROM logged_waste WHERE eventId = :eventId")
    suspend fun getWasteForEvent(eventId: Int): List<LoggedWaste>
}