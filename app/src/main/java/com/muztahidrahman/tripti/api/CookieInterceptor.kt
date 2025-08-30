package com.muztahidrahman.tripti.api

import com.muztahidrahman.tripti.db.sharedpref.SharedPreferencesStorage
import okhttp3.Interceptor
import okhttp3.Response

class CookieInterceptor(private val storage: SharedPreferencesStorage): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val cookie = storage.getCookies()

        val requestWithCookies = if (cookie.isNotEmpty()) {
            original.newBuilder()
                .addHeader("Cookie", cookie)
                .addHeader("User-Agent", "Mozilla/5.0 (compatible; TriptiApp/1.0)")
                .build()
        } else {
            original.newBuilder()
                .addHeader("User-Agent", "Mozilla/5.0 (compatible; TriptiApp/1.0)")
                .build()
        }

        val response = chain.proceed(requestWithCookies)
        val setCookieHeaders = response.headers("Set-Cookie")
        if (setCookieHeaders.isNotEmpty()) {
            val newCookies = setCookieHeaders.joinToString("; ") { cookie ->
                cookie.split(";")[0]
            }
            storage.saveCookies(newCookies)
        }

        return response

    }
}