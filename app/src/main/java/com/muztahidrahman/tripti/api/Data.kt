package com.muztahidrahman.tripti.api

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?
)