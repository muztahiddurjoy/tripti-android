package com.muztahidrahman.tripti.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.muztahidrahman.tripti.util.QrCodeGenerator

    @Composable
    fun QRCodeViewer(
        content:String,
        size: Dp = 200.dp,
        modifier: Modifier = Modifier
    ) {
        val qrCodeBitmap = remember(content){
            QrCodeGenerator.generateQrCodeImageBitmap(content)
        }
        Image(bitmap = qrCodeBitmap, contentDescription = "QR Code", modifier = modifier.size(size))
    }
