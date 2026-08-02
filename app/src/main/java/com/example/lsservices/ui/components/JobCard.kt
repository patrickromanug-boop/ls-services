
package com.example.lsservices.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lsservices.data.model.MockJob
import com.example.lsservices.ui.theme.PrimaryBlue
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun JobCard(
    job: MockJob,
    isBookmarked: Boolean,
    isApplied: Boolean,
    onToggleBookmark: () -> Unit,
    onCardClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
        onClick = onCardClicked
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                OrgInitialsBadge(orgName = job.organization, size = 44)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(job.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(job.organization, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                DeadlinePill(deadlineStr = job.deadline)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.padding(start = 56.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(job.location, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Text("•", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                Text(job.jobType, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Text("•", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                Text("${job.viewsCount} views", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.padding(start = 56.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onCardClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(if (isApplied) "View details (Applied)" else "View details", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onToggleBookmark, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) PrimaryBlue else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun OrgInitialsBadge(orgName: String, size: Int = 40) {
    val initials = orgName.split(" ").filter { it.isNotEmpty() }.take(2).map { it.first().uppercase() }.joinToString("")
    val bgColor = Color(0xFFE8F0FE)
    val textColor = Color(0xFF1A73E8)
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(bgColor, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(initials, fontSize = (size * 0.38f).sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}

@Composable
fun DeadlinePill(deadlineStr: String) {
    val daysRemaining = remember(deadlineStr) {
        try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val deadlineDate = LocalDate.parse(deadlineStr, formatter)
            val today = LocalDate.now()
            if (deadlineDate.isBefore(today)) -1L else ChronoUnit.DAYS.between(today, deadlineDate)
        } catch (e: Exception) { -1L }
    }
    val isUrgent = daysRemaining in 0..3
    val isExpired = daysRemaining < 0
    val bgColor = when {
        isExpired -> Color(0xFFF5F5F5)
        isUrgent -> Color(0xFFFFEBEE)
        else -> Color(0xFFECEFF1)
    }
    val textColor = when {
        isExpired -> Color(0xFF757575)
        isUrgent -> Color(0xFFC62828)
        else -> Color(0xFF455A64)
    }
    val text = when {
        isExpired -> "Expired"
        daysRemaining == 0L -> "Deadline TODAY"
        daysRemaining == 1L -> "1 day left"
        else -> "$daysRemaining days left"
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}
