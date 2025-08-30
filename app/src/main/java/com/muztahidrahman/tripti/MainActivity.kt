// MainActivity.kt
package com.muztahidrahman.tripti

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.muztahidrahman.tripti.db.sharedpref.SharedPreferencesStorage
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

