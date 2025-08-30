package com.muztahidrahman.tripti.db.room
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val menuType: String,
    val ids: List<String>,
    val dates: List<String>,
    val ingredients: List<String>,
    val lastUpdated: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
)

@Entity(tableName = "today_orders")
data class TodayOrder(
    @PrimaryKey val id: String,
    val mealName: String,
    val mealType: String,
    val orderDate: String,
    val status: String,
    val qrCodeId: String?,
    val lastUpdated: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
)

@Entity(tableName = "qr_codes")
data class QrCodeData(
    @PrimaryKey val qrId: String,
    val mealDescription: String,
    val lastUpdated: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
)

@Entity(tableName = "sync_status")
data class SyncStatus(
    @PrimaryKey val id: Int = 1,
    val lastSyncTime: String,
    val isDataAvailable: Boolean,
    val nextSyncTime: String
)