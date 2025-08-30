package com.muztahidrahman.tripti.db.sharedpref

import android.content.Context
import android.util.Log
import android.webkit.CookieManager
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SharedPreferencesStorage(private val context: Context) : SharedPreferenceManager {

    private val sharedPref = context.getSharedPreferences("tripti_storage", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_USER_INFO = "user_info"
        private const val KEY_COOKIES = "cookies"
        private const val KEY_DAILY_CODES_PREFIX = "daily_codes_"
        private const val KEY_ALL_DATES = "all_dates"
    }

    override fun saveCookies(cookies: String){
        sharedPref.edit().putString(KEY_COOKIES, cookies).apply()
        Log.d("Storage", "Cookies saved: ${cookies.split(";").size} cookies")
        Unit
    }

    override fun getCookies(): String {
        val json = sharedPref.getString(KEY_COOKIES, null)
        return json ?: ""
    }




    private fun getAllSavedDates(): Set<String> {
        return sharedPref.getStringSet(KEY_ALL_DATES, emptySet()) ?: emptySet()
    }

    override suspend fun clearAllData() = withContext(Dispatchers.IO) {
        val manager = CookieManager.getInstance()
        manager.removeAllCookies(null)
        sharedPref.edit().clear().apply()
        Log.d("Storage", "All storage data cleared")
        Unit
    }
}