package com.example.litterboom.data

import android.content.Context

object SessionManager {

    private const val PREFERENCES_FILE = "com.example.litterboom.PREFERENCES"
    private const val USER_ID_KEY = "logged_in_user_id"
    private const val TOKEN_KEY = "auth_token"

    fun saveUserSession(context: Context, user: User) {
        val prefs = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putInt(USER_ID_KEY, user.id)
            apply()
        }
    }

    fun clearSession(context: Context) {
        val prefs = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            remove(USER_ID_KEY)
            remove(TOKEN_KEY)
            apply()
        }
    }

    fun getSavedUserId(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
        return prefs.getInt(USER_ID_KEY, -1)
    }

    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString(TOKEN_KEY, token)
            apply()
        }
    }

    fun getToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
        return prefs.getString(TOKEN_KEY, null)
    }
}
