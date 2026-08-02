
package com.example.lsservices.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lsservices.ui.theme.AccentOrange
import com.example.lsservices.ui.theme.PrimaryBlue

const val LS_SERVICES_WHATSAPP_NUMBER = "256771234567"

@Composable
fun LSLogoLockup(logoSize: Float = 24f, showPill: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(logoSize.dp)
                .background(PrimaryBlue, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("LS", color = Color.White, fontWeight = FontWeight.Black, fontSize = (logoSize * 0.5f).sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text("LS Services", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        if (showPill) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .background(AccentOrange, RoundedCornerShape(20.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("JOBS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
