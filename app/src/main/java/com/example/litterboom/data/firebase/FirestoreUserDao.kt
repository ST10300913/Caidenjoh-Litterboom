package com.example.litterboom.data.firebase

import android.util.Patterns
import com.example.litterboom.data.User
import com.example.litterboom.data.UserDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirestoreUserDao : UserDao {

    private val auth: FirebaseAuth = FirebaseModule.auth
    private val usersCol = FirebaseModule.db.collection("users")

    private fun isEmail(s: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(s).matches()

    override suspend fun insertUser(user: User) {
        val username = user.username
        val password = user.password

        if (isEmail(username)) {
            // Email-style user -> create FirebaseAuth account + user doc
            try {
                val result = auth.createUserWithEmailAndPassword(username, password).await()
                val uid = result.user?.uid ?: return
                usersCol.document(uid).set(
                    mapOf(
                        "email" to username,
                        "role" to user.role,
                        "createdAt" to FieldValue.serverTimestamp()
                    ),
                    SetOptions.merge()
                ).await()
            } catch (e: Exception) {
                // If account exists, just sign in and upsert role
                val res = auth.signInWithEmailAndPassword(username, password).await()
                val uid = res.user?.uid ?: return
                usersCol.document(uid).set(
                    mapOf(
                        "email" to username,
                        "role" to user.role,
                        "updatedAt" to FieldValue.serverTimestamp()
                    ),
                    SetOptions.merge()
                ).await()
            }
        } else {
            // Username-only user -> store credentials in Firestore (keeps old workflow)
            // NOTE: This is plaintext like your Room version. Fine for parity; harden later if needed.
            usersCol.document("uname_$username").set(
                mapOf(
                    "username" to username,
                    "password" to password,
                    "role" to user.role,
                    "createdAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            ).await()
            // Optional: ensure there is an authenticated Firebase session (anonymous)
            if (auth.currentUser == null) {
                runCatching { auth.signInAnonymously().await() }
            }
        }
    }

    override suspend fun getUser(username: String, password: String): User? {
        if (isEmail(username)) {
            // Email -> FirebaseAuth sign-in
            val res = auth.signInWithEmailAndPassword(username, password).await()
            val uid = res.user?.uid ?: return null
            val doc = usersCol.document(uid).get().await()
            val role = doc.getString("role") ?: "User"
            return User(id = 0, username = username, password = password, role = role)
        } else {
            // Username -> Firestore credentials
            // Auto-seed admin/admin on first use to mirror previous Room seed
            if (username == "admin" && password == "admin") {
                val adminDoc = usersCol.document("uname_admin").get().await()
                if (!adminDoc.exists()) {
                    usersCol.document("uname_admin").set(
                        mapOf(
                            "username" to "admin",
                            "password" to "admin",
                            "role" to "Admin",
                            "seededAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }

            val doc = usersCol.document("uname_$username").get().await()
            if (!doc.exists()) return null
            val storedPass = doc.getString("password") ?: return null
            if (storedPass != password) return null
            val role = doc.getString("role") ?: "User"

            // Optional: keep a Firebase session alive (anonymous)
            if (auth.currentUser == null) {
                runCatching { auth.signInAnonymously().await() }
            }

            return User(id = 0, username = username, password = password, role = role)
        }
    }

    override suspend fun getUsersByRole(role: String): List<User> {
        val snap = usersCol.whereEqualTo("role", role).get().await()
        return snap.documents.mapNotNull { d ->
            val email = d.getString("email")
            val uname = d.getString("username")
            val name = email ?: uname ?: return@mapNotNull null
            User(id = 0, username = name, password = "", role = role)
        }
    }

    override suspend fun getAllUsers(): List<User> {
        val snap = usersCol.get().await()
        return snap.documents.mapNotNull { d ->
            val name = d.getString("email") ?: d.getString("username") ?: return@mapNotNull null
            val role = d.getString("role") ?: "User"
            User(id = 0, username = name, password = "", role = role)
        }
    }

    override suspend fun getUserById(id: Int): User? {
        // Compatibility: we don't have numeric ids. Return current signed-in user if we can.
        val current = auth.currentUser ?: return null
        val doc = usersCol.document(current.uid).get().await()
        val name = doc.getString("email") ?: current.email ?: current.displayName ?: return null
        val role = doc.getString("role") ?: "User"
        return User(id = 0, username = name, password = "", role = role)
    }
}
