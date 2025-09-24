package com.example.litterboom.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BagDao {
    @Insert
    suspend fun insertBag(bag: Bag)

    @Query("SELECT * FROM bags WHERE eventId = :eventId")
    suspend fun getBagsByEvent(eventId: Int): List<Bag>

    @Query("UPDATE bags SET isApproved = 1 WHERE eventId = :eventId")
    suspend fun approveBags(eventId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM bags WHERE eventId = :eventId AND isApproved = 1)")
    suspend fun areBagsApproved(eventId: Int): Boolean
}