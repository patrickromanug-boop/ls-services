package com.example.lsservices.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ls_services_prefs", Context.MODE_PRIVATE)

    var isLoggedIn: Boolean get() = prefs.getBoolean("is_logged_in", false)
        set(value) = prefs.edit().putBoolean("is_logged_in", value).apply()

    var userId: String? get() = prefs.getString("user_id", null)
        set(value) = prefs.edit().putString("user_id", value).apply()

    var userName: String? get() = prefs.getString("user_name", null)
        set(value) = prefs.edit().putString("user_name", value).apply()

    var userRole: String get() = prefs.getString("user_role", "user") ?: "user"
        set(value) = prefs.edit().putString("user_role", value).apply()

    var themePreference: String get() = prefs.getString("theme", "system") ?: "system"
        set(value) = prefs.edit().putString("theme", value).apply()

    var pushToken: String? get() = prefs.getString("push_token", null)
        set(value) = prefs.edit().putString("push_token", value).apply()

    var hasCompletedOnboarding: Boolean get() = prefs.getBoolean("onboarding_done", false)
        set(value) = prefs.edit().putBoolean("onboarding_done", value).apply()

    var hasShownCvOnboarding: Boolean get() = prefs.getBoolean("cv_onboarding_shown", false)
        set(value) = prefs.edit().putBoolean("cv_onboarding_shown", value).apply()

    var guestBookmarks: Set<String> get() = prefs.getStringSet("guest_bookmarks", emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet("guest_bookmarks", value).apply()

    var appliedJobs: Set<String> get() = prefs.getStringSet("applied_jobs", emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet("applied_jobs", value).apply()

    var reportedJobs: Set<String> get() = prefs.getStringSet("reported_jobs", emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet("reported_jobs", value).apply()

    var cachedJobsJson: String? get() = prefs.getString("cached_jobs", null)
        set(value) = prefs.edit().putString("cached_jobs", value).apply()

    var cachedJobsTimestamp: Long get() = prefs.getLong("cached_jobs_ts", 0L)
        set(value) = prefs.edit().putLong("cached_jobs_ts", value).apply()

    var cachedProfileJson: String? get() = prefs.getString("cached_profile", null)
        set(value) = prefs.edit().putString("cached_profile", value).apply()

    var cachedApplicationsJson: String? get() = prefs.getString("cached_applications", null)
        set(value) = prefs.edit().putString("cached_applications", value).apply()

    var pendingReferralCode: String? get() = prefs.getString("pending_referral_code", null)
        set(value) = prefs.edit().putString("pending_referral_code", value).apply()

    var servicesPopupDismissedUntil: Long get() = prefs.getLong("services_popup_dismissed_until", 0L)
        set(value) = prefs.edit().putLong("services_popup_dismissed_until", value).apply()

    var notifiedJobIds: Set<String> get() = prefs.getStringSet("notified_job_ids", emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet("notified_job_ids", value).apply()

    var hasCompletedInitialJobSync: Boolean get() = prefs.getBoolean("initial_job_sync", false)
        set(value) = prefs.edit().putBoolean("initial_job_sync", value).apply()

    var hasShownNotifPermissionExplanation: Boolean get() = prefs.getBoolean("notif_explanation_shown", false)
        set(value) = prefs.edit().putBoolean("notif_explanation_shown", value).apply()
}
