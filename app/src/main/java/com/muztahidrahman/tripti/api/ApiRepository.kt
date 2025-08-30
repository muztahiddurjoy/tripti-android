package com.muztahidrahman.tripti.api

class TriptiRepository(private val apiService: ApiService) {

    suspend fun getDashboard(): Result<String> {
        return try {
            val htmlContent = apiService.getDashboardPage()
            Result.success(htmlContent)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get dashboard: ${e.message}"))
        }
    }

    suspend fun getProfile(): Result<String> {
        return try {
            val htmlContent = apiService.getProfilePage()
            Result.success(htmlContent)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get profile: ${e.message}"))
        }
    }

    suspend fun getHistory(): Result<String> {
        return try {
            val htmlContent = apiService.getHistoryPage()
            Result.success(htmlContent)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get history: ${e.message}"))
        }
    }

    suspend fun getCustomPage(url: String): Result<String> {
        return try {
            val htmlContent = apiService.getHTMLPage(url)
            Result.success(htmlContent)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get page: ${e.message}"))
        }
    }
}