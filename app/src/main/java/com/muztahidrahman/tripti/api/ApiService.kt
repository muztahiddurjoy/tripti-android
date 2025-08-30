package com.muztahidrahman.tripti.api

import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun getHTMLPage(@Url url: String): String

    // Add specific endpoints for the pages you want
    @GET("student/dashboard")
    suspend fun getDashboardPage(): String

    @GET("student/profile")
    suspend fun getProfilePage(): String

    @GET("student/history")
    suspend fun getHistoryPage(): String
}