
package com.example.lsservices.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.lsservices.ui.AppViewModel
import com.example.lsservices.ui.theme.PrimaryBlue

@Composable
fun OtherServicesPopup(viewModel: AppViewModel) {
    AlertDialog(
        onDismissRequest = { viewModel.dismissServicesPopupLater() },
        confirmButton = {
            TextButton(onClick = { viewModel.dismissServicesPopupPermanently() }) {
                Text("Don't show again", color = PrimaryBlue)
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.dismissServicesPopupLater() }) {
                Text("Maybe later")
            }
        },
        title = { Text("Unlock Career Services", fontWeight = FontWeight.Bold) },
        text = {
            Text("Explore NSSF, TIN, business registration, CV writing, and document vault services.")
        }
    )
}
