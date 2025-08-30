// MainActivity.kt
package com.muztahidrahman.tripti

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import com.muztahidrahman.tripti.db.SharedPreferencesStorage
import com.muztahidrahman.tripti.ui.theme.TriptiTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val storage = SharedPreferencesStorage(this);
        lifecycleScope.launch {
            val cookies = storage.getCookies()
            Log.d("GOT COOKIES",cookies)
            if(cookies.isEmpty()){
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }
            else{
                startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
            }
        }
    }


}

