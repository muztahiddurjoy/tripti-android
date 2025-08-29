package com.muztahidrahman.tripti.db

data class CookieInfo(
    val name: String,
    val value: String,
    val domain: String,
    val path: String = "/",
    val expiresAt: Long? = null,
    val isSecure: Boolean = false,
    val isHttpOnly: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)