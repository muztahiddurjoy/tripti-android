package com.muztahidrahman.tripti.db.room
import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

// FoodDatabase.kt
@Database(
    entities = [FoodItemEntity::class, TodayOrderEntity::class, SyncStatusEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FoodDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao
    abstract fun todayOrderDao(): TodayOrderDao
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

@Entity(tableName = "food_items")
data class FoodItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val menuType: String,
    val ids: List<String>,
    val dates: List<String>,
    val ingredients: List<String>,
    val lastUpdated: String
)

@Entity(tableName = "today_orders")
data class TodayOrderEntity(
    @PrimaryKey val id: String,
    val mealName: String,
    val mealType: String,
    val orderDate: String,
    val status: String,
    val qrCodeId: String?,
    val lastUpdated: String
)

@Entity(tableName = "sync_status")
data class SyncStatusEntity(
    @PrimaryKey val id: Int = 1,
    val lastSyncTime: String,
    val isDataAvailable: Boolean,
    val nextSyncTime: String
)