package com.example.litterboom.data

object CurrentUserManager {
    var currentUser: User? = null
        private set

    fun login(user: User) {
        currentUser = user
    }

    fun logout() {
        currentUser = null
    }

    fun isAdmin(): Boolean = currentUser?.role == "Admin"
}