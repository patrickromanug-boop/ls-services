package com.example.lsservices.data.remote

import android.app.Application
import android.util.Log
import com.example.lsservices.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class SupabaseClient(private val app: Application) {

    companion object {
        private const val TAG = "SupabaseClient"
    }

    val isRealConfigActive = true   // set to false for pure offline mode

    sealed class AuthResult {
        data class Success(val user: AuthUser, val profile: UserProfile) : AuthResult()
        data class Error(val message: String) : AuthResult()
    }
    data class AuthUser(val id: String, val email: String)

    sealed class ApiResult<out T> {
        data class Success<T>(val data: T) : ApiResult<T>()
        data class Error(val message: String) : ApiResult<Nothing>()
    }

    // ── Auth (demo) ──
    suspend fun login(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        val uid = UUID.nameUUIDFromBytes(email.toByteArray()).toString()
        val profile = UserProfile(id = uid, fullName = email.substringBefore("@"), role = "user")
        AuthResult.Success(AuthUser(uid, email), profile)
    }

    suspend fun loginWithGoogle(email: String, name: String): AuthResult = withContext(Dispatchers.IO) {
        val uid = UUID.nameUUIDFromBytes(email.toByteArray()).toString()
        val profile = UserProfile(id = uid, fullName = name, role = "user")
        AuthResult.Success(AuthUser(uid, email), profile)
    }

    suspend fun signUp(email: String, password: String, fullName: String, phone: String): AuthResult = withContext(Dispatchers.IO) {
        val uid = UUID.randomUUID().toString()
        val profile = UserProfile(id = uid, fullName = fullName, phone = phone, role = "user")
        AuthResult.Success(AuthUser(uid, email), profile)
    }

    suspend fun forgotPassword(email: String): ApiResult<String> = ApiResult.Success("Reset link sent")

    fun logout() { /* clear local tokens if any */ }

    // ── Jobs ──
    suspend fun fetchJobs(): ApiResult<List<MockJob>> = withContext(Dispatchers.IO) {
        // In real app, call Supabase; returns empty to fall back to cache
        ApiResult.Success(emptyList())
    }

    suspend fun insertJob(job: MockJob): ApiResult<Unit> = ApiResult.Success(Unit)
    suspend fun deleteJob(jobId: String): ApiResult<Unit> = ApiResult.Success(Unit)

    // ── Profile ──
    suspend fun fetchProfile(userId: String): ApiResult<UserProfile> = withContext(Dispatchers.IO) {
        ApiResult.Success(UserProfile(id = userId, fullName = "User", role = "user"))
    }

    suspend fun updateProfile(profile: UserProfile): ApiResult<UserProfile> = ApiResult.Success(profile)

    // ── Subscription ──
    suspend fun fetchSubscription(userId: String): ApiResult<UserSubscription> = withContext(Dispatchers.IO) {
        ApiResult.Success(UserSubscription(id = UUID.randomUUID().toString(), userId = userId, planTier = "trial", status = "trial",
            notifDailyLimit = 5, appliesMonthlyLimit = 3, categoriesLimit = 2))
    }

    suspend fun updateSubscription(sub: UserSubscription): ApiResult<UserSubscription> = ApiResult.Success(sub)

    // ── Documents ──
    suspend fun uploadFile(bucket: String, path: String, data: ByteArray, mime: String): ApiResult<String> =
        ApiResult.Success("https://mock-url/$bucket/$path")

    suspend fun insertUserDocument(userId: String, type: String, url: String): ApiResult<UserDocument> =
        ApiResult.Success(UserDocument(UUID.randomUUID().toString(), userId, type, url))

    suspend fun fetchUserDocuments(userId: String): ApiResult<List<UserDocument>> = ApiResult.Success(emptyList())
    suspend fun deleteUserDocument(docId: String, fileUrl: String): ApiResult<Unit> = ApiResult.Success(Unit)

    // ── Applications ──
    suspend fun fetchUserApplications(userId: String): ApiResult<List<UserApplication>> = ApiResult.Success(emptyList())

    suspend fun insertApplication(userId: String, jobId: String, cvUrl: String, docs: List<String>,
                                  title: String, org: String): ApiResult<UserApplication> =
        ApiResult.Success(UserApplication(id = UUID.randomUUID().toString(), userId = userId, jobId = jobId,
            generatedCvUrl = cvUrl, documentsAttached = docs, status = "pending",
            appliedAt = java.time.Instant.now().toString(), jobTitle = title, jobOrganization = org))

    // ── Referrals ──
    suspend fun fetchReferrals(userId: String): ApiResult<List<ReferralRecord>> = ApiResult.Success(emptyList())
    suspend fun ensureReferralCode(userId: String, name: String): String = "LS${name.take(4).uppercase()}${(1000..9999).random()}"
    suspend fun completeReferralReward(userId: String): ApiResult<Boolean> = ApiResult.Success(false)
    suspend fun recordPendingReferral(userId: String, code: String): ApiResult<Unit> = ApiResult.Success(Unit)

    // ── Misc ──
    suspend fun updateHideServicesPopup(userId: String, hide: Boolean): ApiResult<Unit> = ApiResult.Success(Unit)
    suspend fun changePassword(newPassword: String): ApiResult<String> = ApiResult.Success("Password changed")
    suspend fun deleteAccount(userId: String): ApiResult<Unit> = ApiResult.Success(Unit)
    suspend fun upsertPushToken(userId: String, token: String): ApiResult<Unit> = ApiResult.Success(Unit)
}
