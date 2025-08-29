package com.muztahidrahman.tripti

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.muztahidrahman.tripti.db.SharedPreferencesStorage
import kotlinx.coroutines.launch

class DashboardActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val storage = SharedPreferencesStorage(this);
        lifecycleScope.launch {
            val cookies = storage.getCookies()
            if(cookies.isEmpty()){
                startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
            }
        }
    }
}