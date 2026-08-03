package com.example.lsservices.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lsservices.data.model.MockJob
import com.example.lsservices.data.model.UserApplication
import com.example.lsservices.ui.AppViewModel
import com.example.lsservices.ui.theme.PrimaryBlue

@Composable
fun AdminPortalScreen(viewModel: AppViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    if (userProfile?.role != "admin") {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Text("Access denied. Admin only.")
        }
        return
    }

    var selectedSection by remember { mutableStateOf("jobs") } // "jobs", "applications", "ads"

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Admin Portal", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = selectedSection == "jobs", onClick = { selectedSection = "jobs" }, label = { Text("Jobs") })
            FilterChip(selected = selectedSection == "applications", onClick = { selectedSection = "applications" }, label = { Text("Applications") })
            FilterChip(selected = selectedSection == "ads", onClick = { selectedSection = "ads" }, label = { Text("Ads") })
        }
        Spacer(modifier = Modifier.height(16.dp))

        when (selectedSection) {
            "jobs" -> AdminJobsSection(viewModel)
            "applications" -> AdminApplicationsSection(viewModel)
            "ads" -> AdminAdsSection(viewModel)
        }
    }
}

@Composable
fun AdminJobsSection(viewModel: AppViewModel) {
    val jobs by viewModel.jobs.collectAsState()
    var showNewJobDialog by remember { mutableStateOf(false) }

    Button(onClick = { showNewJobDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)) {
        Icon(Icons.Default.Add, null); Spacer(Modifier.width(4.dp)); Text("Post New Job")
    }

    Spacer(modifier = Modifier.height(8.dp))
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(jobs) { job ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(job.title, fontWeight = FontWeight.Bold)
                    Text(job.organization)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { /* edit */ }) { Text("Edit") }
                        TextButton(onClick = { viewModel.deleteJob(job.id) }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                    }
                }
            }
        }
    }

    if (showNewJobDialog) {
        var title by remember { mutableStateOf("") }
        var org by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNewJobDialog = false },
            title = { Text("Post New Job") },
            text = {
                Column {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Job Title") })
                    OutlinedTextField(value = org, onValueChange = { org = it }, label = { Text("Organization") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.postJob(MockJob(
                        id = java.util.UUID.randomUUID().toString(),
                        title = title,
                        organization = org,
                        location = "Kampala",
                        jobType = "Full-time",
                        category = "Engineering & IT"
                    )) { showNewJobDialog = false }
                }) { Text("Post") }
            },
            dismissButton = { TextButton(onClick = { showNewJobDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun AdminApplicationsSection(viewModel: AppViewModel) {
    val apps by viewModel.allApplications.collectAsState()
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(apps) { app ->
            Card {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("${app.candidateName ?: "N/A"} → ${app.jobTitle}", fontWeight = FontWeight.Bold)
                    Text("Status: ${app.status}")
                    Row {
                        TextButton(onClick = { viewModel.updateApplicationStatus(app.id, "shortlisted") }) { Text("Shortlist") }
                        TextButton(onClick = { viewModel.updateApplicationStatus(app.id, "rejected") }) { Text("Reject", color = MaterialTheme.colorScheme.error) }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAdsSection(viewModel: AppViewModel) {
    // Basic placeholders for company ads management
    Text("Ads management coming soon.")
}
