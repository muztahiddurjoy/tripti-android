package com.muztahidrahman.tripti.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.muztahidrahman.tripti.api.ApiClient
import com.muztahidrahman.tripti.db.room.FoodItemEntity
import com.muztahidrahman.tripti.db.room.FoodRepository
import com.muztahidrahman.tripti.db.room.TodayOrderEntity
import com.muztahidrahman.tripti.db.sharedpref.SharedPreferencesStorage
import com.muztahidrahman.tripti.util.FoodScheduleParser
import java.util.concurrent.TimeUnit

// FoodSyncWorker.kt
class FoodSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val repository = FoodRepository(applicationContext)
            val parser = FoodScheduleParser()
            val storage = SharedPreferencesStorage(applicationContext)

            // Check if we should fetch data
            if (!repository.shouldFetchData()) {
                return Result.success()
            }

            // Get cookies and make API call (similar to your DashboardActivity)
            val cookies = storage.getCookies()
            if (cookies.isEmpty()) {
                return Result.failure()
            }

            val apiClient = ApiClient(storage)
            val dashboard = apiClient.apiService.getDashboardPage()
            val parsedData = parser.parseHtml(dashboard)

            // Convert to entities and save to database
            val foodItems = parsedData.foodItems.map { foodItem ->
                FoodItemEntity(
                    id = foodItem.ids.firstOrNull() ?: System.currentTimeMillis().toString(),
                    name = foodItem.name,
                    description = foodItem.description,
                    menuType = foodItem.menuType,
                    ids = foodItem.ids,
                    dates = foodItem.dates.map { it.toString() },
                    ingredients = foodItem.ingredients,
                    lastUpdated = System.currentTimeMillis().toString()
                )
            }

            val todayOrders = parsedData.todayOrders.map { order ->
                TodayOrderEntity(
                    id = order.qrCodeId ?: System.currentTimeMillis().toString(),
                    mealName = order.mealName,
                    mealType = order.mealType,
                    orderDate = order.orderDate.toString(),
                    status = order.status,
                    qrCodeId = order.qrCodeId,
                    lastUpdated = System.currentTimeMillis().toString()
                )
            }

            repository.saveFoodItems(foodItems)
            repository.saveTodayOrders(todayOrders)

            // Update sync status
            val nextSyncTime = System.currentTimeMillis() + (60 * 60 * 1000) // 1 hour
            repository.updateSyncStatus(
                lastSyncTime = System.currentTimeMillis().toString(),
                isDataAvailable = true,
                nextSyncTime = nextSyncTime.toString()
            )

            Result.success()
        } catch (e: Exception) {
            Log.e("FoodSyncWorker", "Error in background sync", e)
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_WORK_NAME = "FoodSyncWorker"

        fun scheduleSync(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<FoodSyncWorker>(
                1, TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}