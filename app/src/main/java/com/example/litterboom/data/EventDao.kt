package com.example.litterboom.data

interface EventDao {
    suspend fun insertEvent(event: Event)
    suspend fun getAllEvents(): List<Event>
    suspend fun getEventsBetween(start: Long, end: Long): List<Event>
    suspend fun getOpenEvents(): List<Event>
    suspend fun updateEvent(event: Event)
}
