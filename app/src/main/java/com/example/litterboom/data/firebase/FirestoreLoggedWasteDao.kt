package com.example.litterboom.data.firebase

import com.example.litterboom.data.LoggedWaste
import com.example.litterboom.data.LoggedWasteDao
import kotlinx.coroutines.tasks.await

class FirestoreLoggedWasteDao : LoggedWasteDao {

    private val col = FirebaseModule.db.collection("logged_waste")

    override suspend fun insertLoggedWaste(item: LoggedWaste): Long {
        // Use a time-based id to preserve your Int id type
        val newId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        col.document(newId.toString()).set(
            mapOf(
                "id" to newId,
                "eventId" to item.eventId,
                "userId" to item.userId,
                "category" to item.category,
                "subCategory" to item.subCategory,
                "details" to item.details
            )
        ).await()
        return newId.toLong()
    }

    override suspend fun updateLoggedWaste(item: LoggedWaste) {
        val id = item.id.takeIf { it != 0 }?.toString() ?: return
        col.document(id).update(
            mapOf(
                "eventId" to item.eventId,
                "userId" to item.userId,
                "category" to item.category,
                "subCategory" to item.subCategory,
                "details" to item.details
            )
        ).await()
    }

    override suspend fun deleteLoggedWaste(item: LoggedWaste) {
        val id = item.id.takeIf { it != 0 }?.toString() ?: return
        col.document(id).delete().await()
    }

    override suspend fun getWasteForEvent(eventId: Int): List<LoggedWaste> {
        val snap = col.whereEqualTo("eventId", eventId).get().await()
        return snap.documents.mapNotNull { d ->
            LoggedWaste(
                id = (d.getLong("id") ?: 0L).toInt(),
                eventId = (d.getLong("eventId") ?: return@mapNotNull null).toInt(),
                userId = (d.getLong("userId") ?: -1L).toInt(),
                category = d.getString("category") ?: "",
                subCategory = d.getString("subCategory") ?: "",
                details = d.getString("details") ?: "{}"
            )
        }.sortedByDescending { it.id }
    }

    override suspend fun getLoggedWasteById(id: Int): LoggedWaste? {
        val doc = col.document(id.toString()).get().await()
        if (!doc.exists()) return null
        return LoggedWaste(
            id = (doc.getLong("id") ?: 0L).toInt(),
            eventId = (doc.getLong("eventId") ?: 0L).toInt(),
            userId = (doc.getLong("userId") ?: -1L).toInt(),
            category = doc.getString("category") ?: "",
            subCategory = doc.getString("subCategory") ?: "",
            details = doc.getString("details") ?: "{}"
        )
    }
}
