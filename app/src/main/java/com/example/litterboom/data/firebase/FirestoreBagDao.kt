package com.example.litterboom.data.firebase

import com.example.litterboom.data.Bag
import com.example.litterboom.data.BagDao
import kotlinx.coroutines.tasks.await

class FirestoreBagDao : BagDao {

    private val col = FirebaseModule.db.collection("bags")

    override suspend fun insertBag(bag: Bag) {
        val id = "${bag.eventId}-${bag.bagNumber}"
        col.document(id).set(
            mapOf(
                "bagId" to bag.bagId,
                "eventId" to bag.eventId,
                "bagNumber" to bag.bagNumber,
                "weight" to bag.weight,
                "isApproved" to bag.isApproved
            )
        ).await()
    }

    override suspend fun getBagsByEvent(eventId: Int): List<Bag> {
        val snap = col.whereEqualTo("eventId", eventId).get().await()
        return snap.documents.mapNotNull { d ->
            Bag(
                bagId = (d.getLong("bagId") ?: 0L).toInt(),
                eventId = (d.getLong("eventId") ?: return@mapNotNull null).toInt(),
                bagNumber = (d.getLong("bagNumber") ?: 0L).toInt(),
                weight = d.getDouble("weight") ?: 0.0,
                isApproved = d.getBoolean("isApproved") ?: false
            )
        }.sortedBy { it.bagNumber }
    }

    override suspend fun approveBags(eventId: Int) {
        val snap = col.whereEqualTo("eventId", eventId).get().await()
        val batch = FirebaseModule.db.batch()
        snap.documents.forEach { d -> batch.update(d.reference, "isApproved", true) }
        batch.commit().await()
    }

    override suspend fun areBagsApproved(eventId: Int): Boolean {
        val snap = col.whereEqualTo("eventId", eventId).whereEqualTo("isApproved", true).limit(1).get().await()
        return !snap.isEmpty
    }
}
