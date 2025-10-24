package com.example.litterboom.data

interface BagDao {
    suspend fun insertBag(bag: Bag)
    suspend fun getBagsByEvent(eventId: Int): List<Bag>
    suspend fun approveBags(eventId: Int)
    suspend fun areBagsApproved(eventId: Int): Boolean
}
