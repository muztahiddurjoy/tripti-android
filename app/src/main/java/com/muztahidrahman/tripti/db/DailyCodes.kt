package com.muztahidrahman.tripti.db

data class DailyCodes(
    val date: String, // Format: yyyy-MM-dd
    val codes: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)
