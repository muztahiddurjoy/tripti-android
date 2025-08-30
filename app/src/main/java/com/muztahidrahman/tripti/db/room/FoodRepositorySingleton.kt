package com.muztahidrahman.tripti.db.room

import android.content.Context

object FoodRepositorySingleton {
    private var repository: FoodRepository? = null

    fun initialize(context: Context) {
        if (repository == null) {
            val database = FoodDatabase.getDatabase(context)
            repository = FoodRepository(context)
        }
    }

    fun getInstance(): FoodRepository {
        return repository ?: throw IllegalStateException("Repository not initialized. Call initialize() first.")
    }
}