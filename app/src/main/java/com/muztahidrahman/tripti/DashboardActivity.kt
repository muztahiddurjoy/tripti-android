package com.muztahidrahman.tripti

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ScrollView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.muztahidrahman.tripti.api.ApiClient
import com.muztahidrahman.tripti.api.DashboardData
import com.muztahidrahman.tripti.db.SharedPreferencesStorage
import com.muztahidrahman.tripti.ui.theme.TriptiTheme
import com.muztahidrahman.tripti.util.FoodScheduleParser
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

            val apiClient = ApiClient(storage)
            var dashboardcontent = ""
            try{
                val dashboard = apiClient.apiService.getDashboardPage();
                dashboardcontent = dashboard
                val parser = FoodScheduleParser()
                val parsedData = parser.parseHtml(dashboard)



//                Log.d("HTTP MUZ",dashboard)
                Log.d("HTTP MUZ",parsedData.foodItems.toString())
            }
            catch (e:Exception){
                Log.d("HTTP MUZ",e.message.toString())
            }

            setContent {
                var dashboardState by remember { mutableStateOf("") }
                TriptiTheme {

                    Scaffold(
                        topBar = {
                            Text(
                                text = "Dashboard",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                style = androidx.compose.ui.text.TextStyle(
                                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    ) { paddingValues ->
                        Column(modifier = Modifier.padding(paddingValues).verticalScroll(
                            rememberScrollState()
                        )) {
                            Text("Cookies: $cookies\n\n+nDashboard Content:\n$dashboardcontent",
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .padding(16.dp)
                            )
                        }
                    }

                }
            }


        }
    }
}