package com.example.litterboom.data

import android.content.Context
import com.example.litterboom.data.firebase.FirestoreBagDao
import com.example.litterboom.data.firebase.FirestoreEventDao
import com.example.litterboom.data.firebase.FirestoreLoggedWasteDao
import com.example.litterboom.data.firebase.FirestoreWasteDao
import com.example.litterboom.data.firebase.FirestoreUserDao

/**
 * Room replacement shim.
 * Provides Firestore-backed DAO implementations with the SAME method signatures.
 */
class AppDatabase private constructor() {

    fun userDao(): UserDao = FirestoreUserDao()
    fun eventDao(): EventDao = FirestoreEventDao()
    fun wasteDao(): WasteDao = FirestoreWasteDao()
    fun bagDao(): BagDao = FirestoreBagDao()
    fun loggedWasteDao(): LoggedWasteDao = FirestoreLoggedWasteDao()

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
