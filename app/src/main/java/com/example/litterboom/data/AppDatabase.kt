package com.example.litterboom.data

import android.content.Context
import com.example.litterboom.data.api.*

/**
 * Room replacement shim.
 * Provides API-backed DAO implementations with the SAME method signatures.
 */
class AppDatabase private constructor() {

    fun userDao(): UserDao = ApiUserDao()
    fun eventDao(): EventDao = ApiEventDao()
    fun wasteDao(): WasteDao = ApiWasteDao()
    fun bagDao(): BagDao = ApiBagDao()
    fun loggedWasteDao(): LoggedWasteDao = ApiLoggedWasteDao()

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(@Suppress("UNUSED_PARAMETER") context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = AppDatabase()
                INSTANCE = instance
                instance
            }
        }
    }
}
