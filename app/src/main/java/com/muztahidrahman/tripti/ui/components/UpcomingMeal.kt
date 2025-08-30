package com.muztahidrahman.tripti.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ChipElevation
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.muztahidrahman.tripti.util.TodayOrder
import java.time.LocalDateTime

@Composable
fun UpcomingMeal(
    data: TodayOrder,
) {
    Card {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.padding(15.dp)) {
            Text(data.mealName)
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
                SuggestionChip(
                    onClick = {  },
                    label = { Text(data.mealType) },
                )
            }
            if(data.qrCodeId!=null)
                QRCodeViewer(content = data.qrCodeId, size = 250.dp)
        }
    }
}