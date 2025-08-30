package com.muztahidrahman.tripti

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.muztahidrahman.tripti.api.ApiClient
import com.muztahidrahman.tripti.db.room.FoodItemEntity
import com.muztahidrahman.tripti.db.room.FoodRepository
import com.muztahidrahman.tripti.db.room.TodayOrderEntity
import com.muztahidrahman.tripti.db.sharedpref.SharedPreferencesStorage
import com.muztahidrahman.tripti.ui.components.QRCodeViewer
import com.muztahidrahman.tripti.ui.components.UpcomingMeal
import com.muztahidrahman.tripti.ui.theme.TriptiTheme
import com.muztahidrahman.tripti.util.FoodItem
import com.muztahidrahman.tripti.util.FoodScheduleParser
import com.muztahidrahman.tripti.util.ParsedData
import com.muztahidrahman.tripti.util.TodayOrder
import com.muztahidrahman.tripti.workers.FoodSyncWorker
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

// DashboardActivity.kt
class DashboardActivity: ComponentActivity() {
    private lateinit var repository: FoodRepository
    val nowTime = LocalDateTime.now()
    val currentTime = nowTime.toLocalTime()

    val breakfastStart = LocalTime.of(7, 30)
    val breakfastEnd = LocalTime.of(8, 45)
    val morningSnackStart = LocalTime.of(8, 45)
    val morningSnackEnd = LocalTime.of(11, 0)
    val lunchStart = LocalTime.of(11, 0)
    val lunchEnd = LocalTime.of(14, 30)
    val eveningSnackStart = LocalTime.of(14, 30)
    val eveningSnackEnd = LocalTime.of(16, 30)
    val dinnerStart = LocalTime.of(16, 30)
    val dinnerEnd = LocalTime.of(21, 30)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = FoodRepository(this)
        val storage = SharedPreferencesStorage(this)
        val parser = FoodScheduleParser()

        var parseData: ParsedData? = null
        var localDataAvailable by mutableStateOf(false)

        // Schedule background sync
        FoodSyncWorker.scheduleSync(this)

        lifecycleScope.launch {
            val cookies = storage.getCookies()
            if(cookies.isEmpty()){
                startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                finish()
                return@launch
            }

            // First try to load from local database
            try {
                val localFoodItems = repository.getFoodItems()
                val localTodayOrders = repository.getTodayOrders()

                if (localFoodItems.isNotEmpty() && localTodayOrders.isNotEmpty()) {
                    localDataAvailable = true
                    parseData = ParsedData(
                        todayOrders = localTodayOrders.map {
                            TodayOrder(it.mealName, it.mealType, LocalDate.parse(it.orderDate), it.status, it.qrCodeId)
                        },
                        foodItems = localFoodItems.map {
                            FoodItem(it.name, it.description, it.menuType, it.ids,
                                it.dates.map { date -> LocalDate.parse(date) }, it.ingredients)
                        },
                        dayViews = emptyList()
                    )
                }
                Log.d("LocalData", "Loaded local data: ${localFoodItems.size} food items, ${localTodayOrders.size} orders")
            } catch (e: Exception) {
                Log.d("LocalData", "No local data available")
            }

            // If no local data, fetch from network
            if (parseData == null) {
                val apiClient = ApiClient(storage)
                try {
                    val dashboard = apiClient.apiService.getDashboardPage()
                    parseData = parser.parseHtml(dashboard)

                    // Save to local database for next time
                    val foodItems = parseData.foodItems.map { foodItem ->
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

                    val todayOrders = parseData.todayOrders.map { order ->
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

                } catch (e: Exception) {
                    Log.d("HTTP MUZ", e.message.toString())
                }
            }

            setContent {
                TriptiTheme {
                    Scaffold(
                        topBar = {
                            Text(
                                text = "Dashboard ${if (localDataAvailable) "(Offline)" else ""}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                style = androidx.compose.ui.text.TextStyle(
                                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    ) { paddingValues ->
                        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 20.dp).verticalScroll(
                            rememberScrollState()
                        )) {
                            if(parseData != null) {
                                Text("Today:", modifier = Modifier.padding(top = 10.dp))

                                when {
                                    currentTime.isAfter(breakfastStart) && currentTime.isBefore(breakfastEnd) -> {
                                        parseData.todayOrders.filter { it.mealType.equals("breakfast", true) }.firstOrNull()?.let { UpcomingMeal(it) }
                                    }
                                    currentTime.isAfter(morningSnackStart) && currentTime.isBefore(morningSnackEnd) -> {
                                        parseData.todayOrders.filter { it.mealType.equals("morningsnack", true) }.firstOrNull()?.let { UpcomingMeal(it) }
                                    }
                                    currentTime.isAfter(lunchStart) && currentTime.isBefore(lunchEnd) -> {
                                        parseData.todayOrders.filter { it.mealType.equals("lunch", true) }.firstOrNull()?.let { UpcomingMeal(it) }
                                    }
                                    currentTime.isAfter(eveningSnackStart) && currentTime.isBefore(eveningSnackEnd) -> {
                                        parseData.todayOrders.filter { it.mealType.equals("eveningsnack", true) }.firstOrNull()?.let { UpcomingMeal(it) }
                                    }
                                    currentTime.isAfter(dinnerStart) && currentTime.isBefore(dinnerEnd) -> {
                                        parseData.todayOrders.filter { it.mealType.equals("dinner", true) }.firstOrNull()?.let { UpcomingMeal(it) }
                                    }
                                }


                                for(item in parseData.todayOrders){
                                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().background(
                                        Color.DarkGray,
                                    )) {
                                        Text(
                                            text = "Name: ${item.orderDate}  -- Type: ${item.mealType}",
                                            modifier = Modifier.padding(top = 10.dp)
                                        )
//                                        if (item.qrCodeId != null)
//                                            QRCodeViewer(item.qrCodeId)
                                    }
                                }
                            } else {
                                Text("Could not load data")
                            }
                        }
                    }
                }
            }
        }
    }
}