package com.muztahidrahman.tripti.db.room

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import androidx.room.TypeConverter

@TypeConverters(Converters::class)
@Database(
    entities = [FoodItem::class, TodayOrder::class, QrCodeData::class, SyncStatus::class],
    version = 1,
    exportSchema = false
)
abstract class FoodDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao
    abstract fun todayOrderDao(): TodayOrderDao
    abstract fun qrCodeDao(): QrCodeDao
    abstract fun syncStatusDao(): SyncStatusDao

    companion object {
        @Volatile
        private var INSTANCE: FoodDatabase? = null

        fun getDatabase(context: Context): FoodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodDatabase::class.java,
                    "food_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }
}

@Dao
interface FoodItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foodItems: List<FoodItem>)

    @Query("SELECT * FROM food_items")
    suspend fun getAll(): List<FoodItem>

    @Query("SELECT * FROM food_items WHERE menuType = :menuType")
    suspend fun getByMenuType(menuType: String): List<FoodItem>

    @Query("DELETE FROM food_items")
    suspend fun deleteAll()
}

@Dao
interface TodayOrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<TodayOrder>)

    @Query("SELECT * FROM today_orders")
    suspend fun getAll(): List<TodayOrder>

    @Query("DELETE FROM today_orders")
    suspend fun deleteAll()
}

@Dao
interface QrCodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(qrCodes: List<QrCodeData>)

    @Query("SELECT * FROM qr_codes")
    suspend fun getAll(): Map<String, QrCodeData>

    @Query("SELECT * FROM qr_codes WHERE qrId = :qrId")
    suspend fun getByQrId(qrId: String): QrCodeData?

    @Query("DELETE FROM qr_codes")
    suspend fun deleteAll()
}

@Dao
interface SyncStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(syncStatus: SyncStatus)

    @Query("SELECT * FROM sync_status WHERE id = 1")
    suspend fun get(): SyncStatus?

    @Query("UPDATE sync_status SET lastSyncTime = :lastSyncTime, isDataAvailable = :isDataAvailable, nextSyncTime = :nextSyncTime WHERE id = 1")
    suspend fun update(lastSyncTime: String, isDataAvailable: Boolean, nextSyncTime: String)
}