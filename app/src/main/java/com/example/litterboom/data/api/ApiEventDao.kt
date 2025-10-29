package com.example.litterboom.data.api

import com.example.litterboom.data.Event
import com.example.litterboom.data.EventDao

class ApiEventDao : EventDao {
    private val apiService = ApiClient.apiService

    override suspend fun insertEvent(event: Event) {
        apiService.createEvent(event)
    }

    override suspend fun getAllEvents(): List<Event> =
        apiService.getEvents()

    override suspend fun getEventsBetween(start: Long, end: Long): List<Event> =
        apiService.getEventsBetween(start, end)

    override suspend fun getOpenEvents(): List<Event> =
        apiService.getOpenEvents()

    override suspend fun updateEvent(event: Event) {
        val response = apiService.updateEvent(event.id, event)
        if (!response.isSuccessful) {
            throw Exception("Failed to update event: ${response.message()}")
        }
    }
}
