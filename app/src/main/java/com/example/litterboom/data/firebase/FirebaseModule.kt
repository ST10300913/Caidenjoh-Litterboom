package com.example.litterboom.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.storage.FirebaseStorage

object FirebaseModule {
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance().apply {
            firestoreSettings = firestoreSettings {
                isPersistenceEnabled = true
            }
        }
    }
    val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
}
