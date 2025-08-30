package com.muztahidrahman.tripti.db

interface StorageManager {
    suspend fun saveUserInfo(userInfo: UserInfo)
    suspend fun getUserInfo(): UserInfo?
    fun saveCookies(cookies: String)
    fun getCookies(): String
    suspend fun saveDailyCodes(codes: DailyCodes)
    suspend fun getDailyCodesForDate(date: String): DailyCodes?
    suspend fun getAllDailyCodes(): List<DailyCodes>
    suspend fun clearAllData()
}