package com.muztahidrahman.tripti.db.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FoodRepository(private val database: FoodDatabase) {

    private val foodItemDao = database.foodItemDao()
    private val todayOrderDao = database.todayOrderDao()
    private val qrCodeDao = database.qrCodeDao()
    private val syncStatusDao = database.syncStatusDao()

    suspend fun getSyncStatus(): SyncStatus? {
        return syncStatusDao.get()
    }

    suspend fun updateSyncStatus(lastSyncTime: String, isDataAvailable: Boolean, nextSyncTime: String) {
        syncStatusDao.update(lastSyncTime, isDataAvailable, nextSyncTime)
    }

    suspend fun saveFoodItems(items: List<FoodItem>) {
        foodItemDao.insertAll(items)
    }

    suspend fun getFoodItems(): List<FoodItem> {
        return foodItemDao.getAll()
    }

    suspend fun getFoodItemsByType(menuType: String): List<FoodItem> {
        return foodItemDao.getByMenuType(menuType)
    }

    suspend fun saveTodayOrders(orders: List<TodayOrder>) {
        todayOrderDao.insertAll(orders)
    }

    suspend fun getTodayOrders(): List<TodayOrder> {
        return todayOrderDao.getAll()
    }

    suspend fun saveQrCodes(qrCodes: List<QrCodeData>) {
        qrCodeDao.insertAll(qrCodes)
    }

    suspend fun getQrCode(qrId: String): QrCodeData? {
        return qrCodeDao.getByQrId(qrId)
    }

    suspend fun getAllQrCodes(): Map<String, QrCodeData> {
        return qrCodeDao.getAll()
    }

    suspend fun clearAllData() {
        foodItemDao.deleteAll()
        todayOrderDao.deleteAll()
        qrCodeDao.deleteAll()
    }

    suspend fun shouldFetchData(): Boolean {
        val syncStatus = syncStatusDao.get()
        if (syncStatus == null || !syncStatus.isDataAvailable) {
            return true
        }

        val nextSyncTime = LocalDateTime.parse(syncStatus.nextSyncTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        return LocalDateTime.now().isAfter(nextSyncTime)
    }
}