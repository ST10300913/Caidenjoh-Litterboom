package com.example.litterboom.data

interface LoggedWasteDao {
    suspend fun insertLoggedWaste(item: LoggedWaste): Long
    suspend fun updateLoggedWaste(item: LoggedWaste)
    suspend fun deleteLoggedWaste(item: LoggedWaste)
    suspend fun getWasteForEvent(eventId: Int): List<LoggedWaste>
    suspend fun getLoggedWasteById(id: Int): LoggedWaste?
}
