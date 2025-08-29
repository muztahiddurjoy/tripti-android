package com.muztahidrahman.tripti.db

data class UserInfo(
    val name: String,
    val email: String,
    val loginTimestamp: Long = System.currentTimeMillis()
)