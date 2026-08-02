
package com.example.lsservices.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.lsservices.ui.theme.AccentOrange
import com.example.lsservices.ui.theme.PrimaryBlue

@Composable
fun NotificationExplanationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = PrimaryBlue) },
        title = { Text("Stay Updated on Jobs", fontWeight = FontWeight.Bold) },
        text = { Text("Allow LS Services to send you instant job alerts when vacancies matching your profile are posted.") },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)) {
                Text("Enable Alerts")
            }
        },
        dismissButton = {
            TextButton(onClick = onOpenSettings) { Text("Open Settings") }
            TextButton(onClick = onDismiss) { Text("Not Now") }
        }
    )
}

@Composable
fun UpgradeDialog(
    onDismiss: () -> Unit,
    onUpgradeSuccess: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Unlock Targeted Alerts", fontWeight = FontWeight.Bold) },
        text = { Text("Targeted job alerts require a Basic plan or higher. Upgrade now to receive instant notifications.") },
        confirmButton = {
            Button(onClick = onUpgradeSuccess, colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)) {
                Text("View Plans")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Dismiss") } }
    )
}
