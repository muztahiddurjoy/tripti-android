package com.muztahidrahman.tripti

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.muztahidrahman.tripti.db.sharedpref.SharedPreferencesStorage
import com.muztahidrahman.tripti.ui.theme.TriptiTheme
import com.muztahidrahman.tripti.util.FoodScheduleParser
import com.muztahidrahman.tripti.util.ParsedData
import kotlinx.coroutines.launch

class DashboardActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val storage = SharedPreferencesStorage(this);
        val parser = FoodScheduleParser()
        var parseData: ParsedData? = null
        lifecycleScope.launch {
            val cookies = storage.getCookies()
            if(cookies.isEmpty()){
                startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                finish()
                return@launch
            }

            val apiClient = ApiClient(storage)
            try{
                val dashboard = apiClient.apiService.getDashboardPage();
                parseData = parser.parseHtml(dashboard)
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

                            if(parseData!=null) {

                                Text("Today:",modifier=Modifier.size(20.dp).padding(top=10.dp))
                                for(item in parseData.todayOrders){
                                    Text(text= "Name: ${item.mealName} -- Item Name: ${item.mealName} -- Type: ${item.mealType} -- QR: ${item.qrCodeId}", modifier = Modifier.padding(top=10.dp))
                                }
                                for (item in parseData.foodItems) {
                                    Text(text= "${item.name} -- ${item.dates} -- ${item.menuType}", modifier = Modifier.padding(top=10.dp))
                                }
                            }
                            else{
                                Text("Could not parse the data")
                            }
                        }
                    }

                }
            }


        }
    }
}