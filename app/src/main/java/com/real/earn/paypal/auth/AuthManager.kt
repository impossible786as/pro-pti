package com.real.earn.paypal.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class AuthManager(context: Context) {

    private val prefs: SharedPreferences = createPrefs(context)

    private fun createPrefs(ctx: Context): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(ctx)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                ctx,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // 🟢 ورچوئل / exotic device: Keystore fail → plain prefs fallback
            Log.w("AuthManager", "EncryptedSharedPreferences failed → fallback", e)
            ctx.getSharedPreferences(PREFS_NAME_FALLBACK, Context.MODE_PRIVATE)
        }
    }

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun saveUserId(userId: Long) {
        prefs.edit().putLong(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): Long = prefs.getLong(KEY_USER_ID, 0L)

    fun saveUserName(name: String) {
        prefs.edit().putString(KEY_USERNAME, name).apply()
    }

    fun getUserName(): String = prefs.getString(KEY_USERNAME, "") ?: ""

    fun saveClientId(clientId: Long) {
        prefs.edit().putLong(KEY_CLIENT_ID, clientId).apply()
    }

    fun getClientId(): Long = prefs.getLong(KEY_CLIENT_ID, 0L)

    fun saveOwnerId(ownerId: Long) {
        prefs.edit().putLong(KEY_OWNER_ID, ownerId).apply()
    }

    fun getOwnerId(): Long = prefs.getLong(KEY_OWNER_ID, 0L)

    fun saveIdentifier(identifier: String) {
        prefs.edit().putString(KEY_IDENTIFIER, identifier).apply()
    }

    fun getIdentifier(): String = prefs.getString(KEY_IDENTIFIER, "") ?: ""

    fun isLoggedIn(): Boolean = !getToken().isNullOrEmpty()

    fun isClientMode(): Boolean = getClientId() > 0

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "real_earn_secure_prefs"
        private const val PREFS_NAME_FALLBACK = "real_earn_secure_prefs_fallback"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "user_name"
        private const val KEY_CLIENT_ID = "client_id"
        private const val KEY_OWNER_ID = "owner_id"
        private const val KEY_IDENTIFIER = "identifier"
    }
}
