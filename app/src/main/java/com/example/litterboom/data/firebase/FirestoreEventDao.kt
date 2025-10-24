package com.example.litterboom.data.firebase

import com.example.litterboom.data.Event
import com.example.litterboom.data.EventDao
import kotlinx.coroutines.tasks.await

class FirestoreEventDao : EventDao {

    private val col = FirebaseModule.db.collection("events")

    override suspend fun insertEvent(event: Event) {
        // Preserve your numeric id if provided; otherwise generate one
        val docId = event.id.takeIf { it != 0 }?.toString()
            ?: System.currentTimeMillis().toInt().toString()

        col.document(docId).set(
            mapOf(
                "id" to docId.toInt(),
                "name" to event.name,
                "date" to event.date,
                "location" to event.location,
                "isOpen" to event.isOpen
            )
        ).await()
    }

    override suspend fun getAllEvents(): List<Event> {
        val snap = col.orderBy("date").get().await()
        return snap.documents.mapNotNull { d ->
            Event(
                id = (d.getLong("id") ?: 0L).toInt(),
                name = d.getString("name") ?: return@mapNotNull null,
                date = d.getLong("date") ?: 0L,
                location = d.getString("location") ?: "",
                isOpen = d.getBoolean("isOpen") ?: true
            )
        }
    }

    override suspend fun getEventsBetween(start: Long, end: Long): List<Event> {
        val snap = col
            .whereGreaterThanOrEqualTo("date", start)
            .whereLessThanOrEqualTo("date", end)
            .orderBy("date")
            .get().await()
        return snap.documents.map { d ->
            Event(
                id = (d.getLong("id") ?: 0L).toInt(),
                name = d.getString("name") ?: "",
                date = d.getLong("date") ?: 0L,
                location = d.getString("location") ?: "",
                isOpen = d.getBoolean("isOpen") ?: true
            )
        }
    }

    override suspend fun getOpenEvents(): List<Event> {
        val snap = col.whereEqualTo("isOpen", true).get().await()
        return snap.documents.map { d ->
            Event(
                id = (d.getLong("id") ?: 0L).toInt(),
                name = d.getString("name") ?: "",
                date = d.getLong("date") ?: 0L,
                location = d.getString("location") ?: "",
                isOpen = true
            )
        }.sortedBy { it.date }
    }

    override suspend fun updateEvent(event: Event) {
        val id = event.id.takeIf { it != 0 }?.toString() ?: return
        col.document(id).update(
            mapOf(
                "name" to event.name,
                "date" to event.date,
                "location" to event.location,
                "isOpen" to event.isOpen
            )
        ).await()
    }
}
