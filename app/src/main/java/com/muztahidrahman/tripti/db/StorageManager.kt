package com.muztahidrahman.tripti.db

interface StorageManager {
    suspend fun saveUserInfo(userInfo: UserInfo)
    suspend fun getUserInfo(): UserInfo?
    suspend fun saveCookies(cookies: List<CookieInfo>)
    suspend fun getCookies(): List<CookieInfo>
    suspend fun saveDailyCodes(codes: DailyCodes)
    suspend fun getDailyCodesForDate(date: String): DailyCodes?
    suspend fun getAllDailyCodes(): List<DailyCodes>
    suspend fun clearAllData()
}