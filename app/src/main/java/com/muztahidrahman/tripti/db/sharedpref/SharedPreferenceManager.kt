package com.muztahidrahman.tripti.db.sharedpref

interface SharedPreferenceManager {
    fun saveCookies(cookies: String)
    fun getCookies(): String
    suspend fun clearAllData()
}