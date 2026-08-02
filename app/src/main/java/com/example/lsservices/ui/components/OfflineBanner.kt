
package com.example.lsservices.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lsservices.ui.AppViewModel
import com.example.lsservices.ui.OfflineBannerState

@Composable
fun OfflineBanner(viewModel: AppViewModel) {
    val state by viewModel.offlineBannerState.collectAsState()
    AnimatedVisibility(visible = state != OfflineBannerState.HIDDEN, enter = fadeIn(), exit = fadeOut()) {
        val (bgColor, text) = when (state) {
            OfflineBannerState.OFFLINE -> Color(0xFFFFF3E0) to "You're offline – showing cached vacancies"
            OfflineBannerState.BACK_ONLINE -> Color(0xFFE8F5E9) to "Back online! Syncing latest jobs..."
            else -> Color.Transparent to ""
        }
        Row(
            modifier = Modifier.fillMaxWidth().background(bgColor).padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.WifiOff, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, fontSize = 11.sp, color = Color.DarkGray)
        }
    }
}
