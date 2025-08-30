package com.muztahidrahman.tripti.api

import com.muztahidrahman.tripti.db.SharedPreferencesStorage
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient(storage: SharedPreferencesStorage) {
    private val client = OkHttpClient.Builder()
        .addInterceptor(CookieInterceptor(storage))
        .addInterceptor { chain->
            val original = chain.request()
            val requestWithUserAgent = original.newBuilder()
                .addHeader("User-Agent", "Mozilla/5.0 (compatible; TriptiApp/1.0)")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Language", "en-US,en;q=0.5")
                .build()
            chain.proceed(requestWithUserAgent)
        }
        .sslSocketFactory(SSLBypass.createUnsafeSSLSocketFactory(), SSLBypass.createUnsafeTrustManager())
        .hostnameVerifier(SSLBypass.unsafeHostnameVerifier)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://tripti.bracu.ac.bd") // Changed to www for web pages
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create()) // For String responses
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

}