package com.muztahidrahman.tripti

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.lifecycle.lifecycleScope
import com.muztahidrahman.tripti.db.SharedPreferencesStorage
import com.muztahidrahman.tripti.ui.theme.TriptiTheme
import kotlinx.coroutines.launch

class DashboardActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val storage = SharedPreferencesStorage(this);
        lifecycleScope.launch {
            val cookies = storage.getCookies()
            if(cookies.isEmpty()){
                startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                finish()
                return@launch
            }

            setContent {
                TriptiTheme {
                    Text("Kire dhon")
                    Text("Cookies: $cookies")
                }
            }


        }
    }
}