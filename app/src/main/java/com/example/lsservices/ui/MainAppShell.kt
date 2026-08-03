
package com.example.lsservices.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lsservices.ui.components.OfflineBanner
import com.example.lsservices.ui.components.OtherServicesPopup
import com.example.lsservices.ui.components.NotificationExplanationDialog
import com.example.lsservices.ui.components.UpgradeDialog
import com.example.lsservices.ui.screens.*
import com.example.lsservices.ui.screens.auth.*
import com.example.lsservices.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppShell(viewModel: AppViewModel = viewModel()) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    var currentScreen by remember { mutableStateOf("main") }   // "main", "login", "signup", "forgot_password"

    val context = LocalContext.current

    // Handle back press
    androidx.activity.compose.BackHandler(enabled = currentScreen != "main") {
        when (currentScreen) {
            "signup", "forgot_password" -> currentScreen = "login"
            "login" -> viewModel.showExitConfirmDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screen"
        ) { screen ->
            when (screen) {
                "login" -> LoginScreen(
                    viewModel,
                    onNavigateToSignUp = { currentScreen = "signup" },
                    onNavigateToForgotPassword = { currentScreen = "forgot_password" },
                    onNavigateToHome = { currentScreen = "main" }
                )
                "signup" -> SignUpScreen(
                    viewModel,
                    onNavigateToLogin = { currentScreen = "login" },
                    onNavigateToHome = { currentScreen = "main" }
                )
                "forgot_password" -> ForgotPasswordScreen(
                    viewModel,
                    onNavigateToLogin = { currentScreen = "login" }
                )
                "main" -> MainTabsShell(viewModel, onNavigateToLogin = { currentScreen = "login" })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabsShell(
    viewModel: AppViewModel,
    onNavigateToLogin: () -> Unit
) {
    val activeTab = viewModel.currentTab
    val activeApplyFlowJob = viewModel.activeApplyFlowJob

    // Handle sub-screens inside Profile tab
    if (viewModel.profileSubScreen != "main" && activeTab == "profile") {
        when (viewModel.profileSubScreen) {
            "document_vault" -> { /* Show DocumentVaultScreen, for now placeholder */ }
            "referral" -> ReferralScreen(viewModel, onBack = { viewModel.profileSubScreen = "main" })
            "subscription_comparison" -> PlanComparisonScreen(viewModel, onBack = { viewModel.profileSubScreen = "main" })
            "other_services" -> OtherServicesScreen(onBack = { viewModel.profileSubScreen = "main" })
            "legal_about" -> LegalAndAboutScreen(onBack = { viewModel.profileSubScreen = "main" })
        }
        return
    }

    // Apply flow overlay
    if (activeApplyFlowJob != null) {
        ApplyFlowScreen(viewModel, job = activeApplyFlowJob!!, onDismiss = { viewModel.activeApplyFlowJob = null })
        return
    }

    // Main scaffold with bottom nav
    val tabs = listOf(
        Tab("home", "Home", Icons.Filled.Home, Icons.Outlined.HomeBorder),
        Tab("saved", "Saved", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder),
        Tab("applications", "Apps", Icons.Filled.Assignment, Icons.Outlined.Assignment),
        Tab("profile", "Profile", Icons.Filled.Person, Icons.Outlined.PersonOutline)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                tabs.forEach { tab ->
                    val selected = activeTab == tab.id
                    NavigationBarItem(
                        selected = selected,
                        onClick = { viewModel.selectTab(tab.id) },
                        icon = { Icon(if (selected) tab.selIcon else tab.unselIcon, contentDescription = tab.label) },
                        label = { Text(tab.label, style = MaterialTheme.typography.labelMedium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = PrimaryBlue,
                            indicatorColor = PrimaryBlue
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            OfflineBanner(viewModel)

            Box(modifier = Modifier.weight(1f)) {
                when (activeTab) {
                    "home" -> HomeScreen(viewModel, onNavigateToLogin)
                    "saved" -> SavedScreen(viewModel, onNavigateToHome = { viewModel.selectTab("home") })
                    "applications" -> ApplicationsScreen(viewModel, onNavigateToLogin)
                    "profile" -> ProfileScreen(viewModel, onNavigateToLogin)
                    "admin" -> AdminPortalScreen(viewModel)
                    else -> HomeScreen(viewModel, onNavigateToLogin)
                }
            }
        }
    }

    // Dialogs
    if (viewModel.showExitConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showExitConfirmDialog = false },
            title = { Text("Exit LS Services?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.showExitConfirmDialog = false
                    (context as? android.app.Activity)?.finish()
                }) { Text("Exit") }
            },
            dismissButton = { TextButton(onClick = { viewModel.showExitConfirmDialog = false }) { Text("Cancel") } }
        )
    }

    // Services popup
    val shouldShowServicesPopup by viewModel.shouldShowServicesPopup.collectAsState()   // add this state if needed; otherwise omit
    if (shouldShowServicesPopup) {
        OtherServicesPopup(viewModel)
    }

    if (viewModel.showNotificationExplanationDialog) {
        val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
            contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            viewModel.onNotificationPermissionResult(context, isGranted)
        }
        NotificationExplanationDialog(
            onConfirm = { viewModel.onUserAcceptedNotifExplanation(context, permissionLauncher) },
            onDismiss = { viewModel.onUserDeclinedNotifExplanation() },
            onOpenSettings = { viewModel.openPhoneNotificationSettings(context) }
        )
    }

    if (viewModel.showUpgradePrompt) {
        UpgradeDialog(
            onDismiss = { viewModel.showUpgradePrompt = false },
            onUpgradeSuccess = {
                viewModel.showUpgradePrompt = false
                viewModel.selectTab("profile")
                viewModel.profileSubScreen = "subscription_comparison"
            }
        )
    }
}

private data class Tab(
    val id: String,
    val label: String,
    val selIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselIcon: androidx.compose.ui.graphics.vector.ImageVector
)
