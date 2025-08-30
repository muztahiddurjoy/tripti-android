package com.muztahidrahman.tripti.api

data class MealInfo(
    val mealTime: String, // "Breakfast", "Lunch", "Dinner"
    val foodItems: List<String>,
    val qrCodeUrl: String?,
    val mealTimeRange: String? // Optional: time range like "8:00 AM - 10:00 AM"
)

data class DashboardData(
    val studentName: String,
    val balance: Double,
    val todayMeals: List<MealInfo>,
    val lastUpdated: String
)