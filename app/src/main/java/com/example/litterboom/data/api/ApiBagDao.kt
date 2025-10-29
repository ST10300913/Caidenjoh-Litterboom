package com.example.litterboom.data.api

import com.example.litterboom.data.*

class ApiBagDao : BagDao {
    override suspend fun insertBag(bag: Bag) {
        ApiClient.apiService.postBag(bag)
    }

    override suspend fun getBagsByEvent(eventId: Int): List<Bag> {
        return ApiClient.apiService.getBags(eventId)
    }

    override suspend fun approveBags(eventId: Int) {
        ApiClient.apiService.approveBags(eventId)
    }

    override suspend fun areBagsApproved(eventId: Int): Boolean {
        return ApiClient.apiService.getBagsApproved(eventId)
    }

    override suspend fun getApprovedBagsForEvent(eventId: Int): List<Bag> {
        return ApiClient.apiService.getBags(eventId).filter { it.isApproved }
    }
}
