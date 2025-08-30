package com.muztahidrahman.tripti.db.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// FoodItemDao.kt
@Dao
interface FoodItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foodItems: List<FoodItemEntity>)

    @Query("SELECT * FROM food_items")
    suspend fun getAll(): List<FoodItemEntity>

    @Query("DELETE FROM food_items")
    suspend fun deleteAll()
}

// TodayOrderDao.kt
@Dao
interface TodayOrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<TodayOrderEntity>)

    @Query("SELECT * FROM today_orders")
    suspend fun getAll(): List<TodayOrderEntity>

    @Query("DELETE FROM today_orders")
    suspend fun deleteAll()
}

// SyncStatusDao.kt
@Dao
interface SyncStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(syncStatus: SyncStatusEntity)

    @Query("SELECT * FROM sync_status WHERE id = 1")
    suspend fun get(): SyncStatusEntity?

    @Query("UPDATE sync_status SET lastSyncTime = :lastSyncTime, isDataAvailable = :isDataAvailable, nextSyncTime = :nextSyncTime WHERE id = 1")
    suspend fun update(lastSyncTime: String, isDataAvailable: Boolean, nextSyncTime: String)
}