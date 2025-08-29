package com.muztahidrahman.tripti.db

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SharedPreferencesStorage(private val context: Context) : StorageManager {

    private val sharedPref = context.getSharedPreferences("tripti_storage", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_USER_INFO = "user_info"
        private const val KEY_COOKIES = "cookies"
        private const val KEY_DAILY_CODES_PREFIX = "daily_codes_"
        private const val KEY_ALL_DATES = "all_dates"
    }

    override suspend fun saveUserInfo(userInfo: UserInfo) = withContext(Dispatchers.IO) {
        val json = gson.toJson(userInfo)
        sharedPref.edit().putString(KEY_USER_INFO, json).apply()
        Log.d("Storage", "User info saved: $userInfo")
        Unit
    }

    override suspend fun getUserInfo(): UserInfo? = withContext(Dispatchers.IO) {
        val json = sharedPref.getString(KEY_USER_INFO, null)
        return@withContext if (json != null) {
            try {
                gson.fromJson(json, UserInfo::class.java)
            } catch (e: Exception) {
                Log.e("Storage", "Error parsing user info", e)
                null
            }
        } else null
    }

    override suspend fun saveCookies(cookies: List<CookieInfo>) = withContext(Dispatchers.IO) {
        val json = gson.toJson(cookies)
        sharedPref.edit().putString(KEY_COOKIES, json).apply()
        Log.d("Storage", "Cookies saved: ${cookies.size} cookies")
        Unit
    }

    override suspend fun getCookies(): List<CookieInfo> = withContext(Dispatchers.IO) {
        val json = sharedPref.getString(KEY_COOKIES, null)
        return@withContext if (json != null) {
            try {
                val type = object : TypeToken<List<CookieInfo>>() {}.type
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                Log.e("Storage", "Error parsing cookies", e)
                emptyList()
            }
        } else emptyList()
    }

    override suspend fun saveDailyCodes(codes: DailyCodes) = withContext(Dispatchers.IO) {
        val json = gson.toJson(codes)
        sharedPref.edit()
            .putString("$KEY_DAILY_CODES_PREFIX${codes.date}", json)
            .apply()

        // Save date to list of all dates
        val allDates = getAllSavedDates().toMutableSet()
        allDates.add(codes.date)
        sharedPref.edit()
            .putStringSet(KEY_ALL_DATES, allDates)
            .apply()

        Log.d("Storage", "Daily codes saved for ${codes.date}: ${codes.codes.size} codes")
        Unit
    }

    override suspend fun getDailyCodesForDate(date: String): DailyCodes? = withContext(Dispatchers.IO) {
        val json = sharedPref.getString("$KEY_DAILY_CODES_PREFIX$date", null)
        return@withContext if (json != null) {
            try {
                gson.fromJson(json, DailyCodes::class.java)
            } catch (e: Exception) {
                Log.e("Storage", "Error parsing daily codes for $date", e)
                null
            }
        } else null
    }

    override suspend fun getAllDailyCodes(): List<DailyCodes> = withContext(Dispatchers.IO) {
        val allDates = getAllSavedDates()
        val allCodes = mutableListOf<DailyCodes>()

        allDates.forEach { date ->
            getDailyCodesForDate(date)?.let { codes ->
                allCodes.add(codes)
            }
        }

        return@withContext allCodes.sortedByDescending { it.timestamp }
    }

    private fun getAllSavedDates(): Set<String> {
        return sharedPref.getStringSet(KEY_ALL_DATES, emptySet()) ?: emptySet()
    }

    override suspend fun clearAllData() = withContext(Dispatchers.IO) {
        sharedPref.edit().clear().apply()
        Log.d("Storage", "All storage data cleared")
        Unit
    }
}