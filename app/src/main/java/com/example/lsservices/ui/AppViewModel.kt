
package com.example.lsservices.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lsservices.data.local.PreferencesManager
import com.example.lsservices.data.model.*
import com.example.lsservices.data.remote.SupabaseClient
import com.example.lsservices.util.CacheUtils
import com.example.lsservices.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class OfflineBannerState { HIDDEN, OFFLINE, BACK_ONLINE }

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application)
    private val client = SupabaseClient(application)

    // ── UI State ──
    var currentTab by mutableStateOf("home")
    val tabHistory = mutableStateListOf("home")
    var showExitConfirmDialog by mutableStateOf(false)
    var activeApplyFlowJob by mutableStateOf<MockJob?>(null)
    var showNotificationExplanationDialog by mutableStateOf(false)
    var showUpgradePrompt by mutableStateOf(false)
    var profileSubScreen by mutableStateOf("main")
    var globalJobDetailToShow by mutableStateOf<MockJob?>(null)
    var hasShownWelcomeBannerThisSession by mutableStateOf(false)

    // ── StateFlows ──
    private val _themeMode = MutableStateFlow(prefs.themePreference)
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(prefs.isLoggedIn)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _userSubscription = MutableStateFlow<UserSubscription?>(null)
    val userSubscription: StateFlow<UserSubscription?> = _userSubscription.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _jobs = MutableStateFlow<List<MockJob>>(emptyList())
    val jobs: StateFlow<List<MockJob>> = _jobs.asStateFlow()

    private val _bookmarks = MutableStateFlow<Set<String>>(prefs.guestBookmarks)
    val bookmarks: StateFlow<Set<String>> = _bookmarks.asStateFlow()

    private val _appliedJobs = MutableStateFlow<Set<String>>(prefs.appliedJobs)
    val appliedJobs: StateFlow<Set<String>> = _appliedJobs.asStateFlow()

    private val _userApplications = MutableStateFlow<List<UserApplication>>(emptyList())
    val userApplications: StateFlow<List<UserApplication>> = _userApplications.asStateFlow()

    private val _userDocuments = MutableStateFlow<List<UserDocument>>(emptyList())
    val userDocuments: StateFlow<List<UserDocument>> = _userDocuments.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _offlineBannerState = MutableStateFlow(OfflineBannerState.HIDDEN)
    val offlineBannerState: StateFlow<OfflineBannerState> = _offlineBannerState.asStateFlow()

    val isRealSupabaseConnected: Boolean = client.isRealConfigActive

    // ── Referral & Popup ──
    private val _pendingReferralCode = MutableStateFlow<String?>(prefs.pendingReferralCode)
    val pendingReferralCode: StateFlow<String?> = _pendingReferralCode.asStateFlow()

    private val _shouldShowServicesPopup = MutableStateFlow(false)
    val shouldShowServicesPopup: StateFlow<Boolean> = _shouldShowServicesPopup.asStateFlow()

    private val _hasCompletedOnboarding = MutableStateFlow(prefs.hasCompletedOnboarding)
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    init {
        viewModelScope.launch {
            loadJobsFromCache()
            if (prefs.isLoggedIn) loadUserProfileAndSubscription()
            setupNetworkMonitoring()
        }
    }

    // ── Jobs ──
    private fun loadJobsFromCache() {
        viewModelScope.launch(Dispatchers.IO) {
            val cached = CacheUtils.parseMockJobList(prefs.cachedJobsJson)
            if (cached.isNotEmpty()) _jobs.value = cached
        }
    }

    fun saveJobsToCache(jobs: List<MockJob>) {
        viewModelScope.launch(Dispatchers.IO) {
            prefs.cachedJobsJson = CacheUtils.jobListToJsonString(jobs)
            prefs.cachedJobsTimestamp = System.currentTimeMillis()
        }
    }

    fun toggleBookmark(jobId: String) {
        val current = _bookmarks.value.toMutableSet()
        if (current.contains(jobId)) current.remove(jobId) else current.add(jobId)
        _bookmarks.value = current
        prefs.guestBookmarks = current
    }

    fun applyJob(jobId: String) {
        val current = _appliedJobs.value.toMutableSet()
        current.add(jobId)
        _appliedJobs.value = current
        prefs.appliedJobs = current
    }

    fun getApplicationsSubmittedThisMonth(): Int {
        val now = LocalDate.now()
        return _userApplications.value.count { it.appliedAt.startsWith("${now.year}-${String.format("%02d", now.monthValue)}") }
    }

    fun submitApplication(jobId: String, jobTitle: String, org: String, cvBytes: ByteArray, docs: List<String>, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val uid = prefs.userId ?: run { onResult(false, "Not logged in"); _isLoading.value = false; return@launch }
            val uploadRes = client.uploadFile("generated-cvs", "$uid/cv_${System.currentTimeMillis()}.pdf", cvBytes, "application/pdf")
            if (uploadRes is SupabaseClient.ApiResult.Error) { onResult(false, uploadRes.message); _isLoading.value = false; return@launch }
            val cvUrl = (uploadRes as SupabaseClient.ApiResult.Success).data
            when (val insRes = client.insertApplication(uid, jobId, cvUrl, docs, jobTitle, org)) {
                is SupabaseClient.ApiResult.Success -> {
                    applyJob(jobId)
                    _userApplications.value = listOf(insRes.data) + _userApplications.value
                    _successMessage.value = "Application submitted!"
                    onResult(true, "Success")
                }
                is SupabaseClient.ApiResult.Error -> onResult(false, insRes.message)
            }
            _isLoading.value = false
        }
    }

    // ── Auth ──
    fun login(email: String, password: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val res = client.login(email, password)) {
                is SupabaseClient.AuthResult.Success -> {
                    _isLoggedIn.value = true; prefs.userId = res.profile.id; _userProfile.value = res.profile
                    loadUserProfileAndSubscription(); onComplete(true)
                }
                is SupabaseClient.AuthResult.Error -> { _errorMessage.value = res.message; onComplete(false) }
            }
            _isLoading.value = false
        }
    }

    fun loginWithGoogle(email: String, name: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val res = client.loginWithGoogle(email, name)) {
                is SupabaseClient.AuthResult.Success -> {
                    _isLoggedIn.value = true; prefs.userId = res.profile.id; _userProfile.value = res.profile
                    loadUserProfileAndSubscription(); onComplete(true)
                }
                is SupabaseClient.AuthResult.Error -> { _errorMessage.value = res.message; onComplete(false) }
            }
            _isLoading.value = false
        }
    }

    fun signUp(email: String, password: String, fullName: String, phone: String, referralCode: String?, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val res = client.signUp(email, password, fullName, phone)) {
                is SupabaseClient.AuthResult.Success -> {
                    _isLoggedIn.value = true; prefs.userId = res.profile.id; _userProfile.value = res.profile
                    if (!referralCode.isNullOrBlank()) client.recordPendingReferral(res.profile.id, referralCode)
                    loadUserProfileAndSubscription(); onComplete(true)
                }
                is SupabaseClient.AuthResult.Error -> { _errorMessage.value = res.message; onComplete(false) }
            }
            _isLoading.value = false
        }
    }

    fun forgotPassword(email: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            when (client.forgotPassword(email)) {
                is SupabaseClient.ApiResult.Success -> { _successMessage.value = "Reset link sent"; onComplete(true) }
                is SupabaseClient.ApiResult.Error -> { _errorMessage.value = "Failed"; onComplete(false) }
            }
        }
    }

    fun logout() {
        client.logout()
        _isLoggedIn.value = false; _userProfile.value = null; _userSubscription.value = null
        prefs.isLoggedIn = false; currentTab = "home"
    }

    private fun loadUserProfileAndSubscription() {
        viewModelScope.launch(Dispatchers.IO) {
            val uid = prefs.userId ?: return@launch
            when (val profileRes = client.fetchProfile(uid)) {
                is SupabaseClient.ApiResult.Success -> { _userProfile.value = profileRes.data; prefs.userName = profileRes.data.fullName }
                else -> Log.e("VM", "Profile fetch error")
            }
            when (val subRes = client.fetchSubscription(uid)) {
                is SupabaseClient.ApiResult.Success -> _userSubscription.value = subRes.data
                else -> {}
            }
        }
    }

    // ── Notifications ──
    fun sendTestNotification() {
        NotificationHelper.showJobAlertNotification(getApplication(), "test", "Test Alert", "LS Services", "Kampala", isTargetedMatch = true)
        _successMessage.value = "Test notification sent"
    }

    fun checkAndRequestNotificationPermission(context: Context) { /* simplified */ }

    fun isNotificationPermissionGranted(context: Context): Boolean = true

    // ── UI Helpers ──
    fun setTheme(theme: String) { prefs.themePreference = theme; _themeMode.value = theme }
    fun clearError() { _errorMessage.value = null }
    fun clearSuccess() { _successMessage.value = null }
    fun selectTab(tab: String) { currentTab = tab }
    fun handleNotificationTap(jobId: String) { globalJobDetailToShow = _jobs.value.find { it.id == jobId } }
    fun onJobDetailViewed() { /* trigger popup after 3 views */ }
    fun dismissServicesPopupLater() { prefs.servicesPopupDismissedUntil = System.currentTimeMillis() + 3*24*60*60*1000L }
    fun dismissServicesPopupPermanently() { viewModelScope.launch { /* update profile */ } }
    fun completeOnboarding() { prefs.hasCompletedOnboarding = true; _hasCompletedOnboarding.value = true }
    fun mockUpgradeToTier(tier: String) {
        _userSubscription.value = _userSubscription.value?.copy(planTier = tier, status = "active")
    }
    fun reportJob(jobId: String, reason: String) { /* store locally */ }

    private fun setupNetworkMonitoring() {
        val cm = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        cm?.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) { _isOnline.value = true }
            override fun onLost(network: android.net.Network) { _isOnline.value = false }
        })
    }
}
