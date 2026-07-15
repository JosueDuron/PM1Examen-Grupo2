package com.uth.cloudcontacts.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_LOGIN_TIME = "login_time"
        private const val SESSION_DURATION_MS = 5 * 60 * 1000 // 5 minutos
    }

    fun saveSession(userId: Int) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, userId)
            putLong(KEY_LOGIN_TIME, System.currentTimeMillis())
            apply()
        }
    }

    fun getActiveUserId(): Int {
        val userId = prefs.getInt(KEY_USER_ID, 0)
        val loginTime = prefs.getLong(KEY_LOGIN_TIME, 0)
        val currentTime = System.currentTimeMillis()

        return if (userId != 0 && (currentTime - loginTime) < SESSION_DURATION_MS) {
            userId
        } else {
            0
        }
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
