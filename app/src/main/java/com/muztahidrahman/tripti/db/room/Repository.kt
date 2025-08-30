package com.muztahidrahman.tripti.db.room
import android.content.Context
import com.muztahidrahman.tripti.db.room.FoodDatabase
import com.muztahidrahman.tripti.db.room.FoodItemEntity
import com.muztahidrahman.tripti.db.room.TodayOrderEntity

// FoodRepository.kt
class FoodRepository(context: Context) {
    private val database = FoodDatabase.getDatabase(context)
    private val foodItemDao = database.foodItemDao()
    private val todayOrderDao = database.todayOrderDao()
    private val syncStatusDao = database.syncStatusDao()

    suspend fun saveFoodItems(items: List<FoodItemEntity>) {
        foodItemDao.insertAll(items)
    }

    suspend fun getFoodItems(): List<FoodItemEntity> {
        return foodItemDao.getAll()
    }

    suspend fun saveTodayOrders(orders: List<TodayOrderEntity>) {
        todayOrderDao.insertAll(orders)
    }

    suspend fun getTodayOrders(): List<TodayOrderEntity> {
        return todayOrderDao.getAll()
    }

    suspend fun updateSyncStatus(lastSyncTime: String, isDataAvailable: Boolean, nextSyncTime: String) {
        syncStatusDao.update(lastSyncTime, isDataAvailable, nextSyncTime)
    }

    suspend fun shouldFetchData(): Boolean {
        val syncStatus = syncStatusDao.get()
        return syncStatus == null || !syncStatus.isDataAvailable
    }
}