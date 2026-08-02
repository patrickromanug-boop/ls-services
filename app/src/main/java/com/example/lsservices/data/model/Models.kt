
package com.example.lsservices.data.model

data class MockJob(
    val id: String,
    val title: String,
    val organization: String,
    val location: String,
    val jobType: String,
    val category: String,
    val salary: String = "Negotiable",
    val deadline: String = "",
    val purpose: String = "",
    val requirements: String = "",
    val otherDetails: String = "",
    val opensExternally: Boolean = false,
    val officialLink: String = "https://lsrecruitingservices.com",
    val applicationMethod: String = "auto_apply",
    val requiredDocuments: List<String> = emptyList(),
    val postedBy: String? = null,
    val viewsCount: Int = 10,
    val status: String = "active",
    val createdAt: String = ""
)

data class UserProfile(
    val id: String,
    val fullName: String,
    val phone: String? = null,
    val role: String = "user",
    val education: String = "[]",
    val skills: List<String> = emptyList(),
    val experience: String = "[]",
    val preferredCategories: List<String> = emptyList(),
    val preferredLocations: List<String> = emptyList(),
    val themePreference: String = "system",
    val hideServicesPopup: Boolean = false,
    val referralCode: String? = null,
    val referredBy: String? = null,
    val notifyAllJobs: Boolean = true,
    val notifyMatchingPreferences: Boolean = false
)

data class UserSubscription(
    val id: String,
    val userId: String,
    val planTier: String = "free",
    val status: String = "active",
    val trialEndsAt: String? = null,
    val renewalDate: String? = null,
    val notifDailyLimit: Int? = null,
    val appliesMonthlyLimit: Int? = null,
    val categoriesLimit: Int? = null
)

data class UserApplication(
    val id: String,
    val userId: String,
    val jobId: String,
    val generatedCvUrl: String = "",
    val documentsAttached: List<String> = emptyList(),
    val status: String = "pending",
    val appliedAt: String = "",
    val jobTitle: String? = null,
    val jobOrganization: String? = null,
    val candidateName: String? = null,
    val candidateEmail: String? = null,
    val candidatePhone: String? = null,
    val adminNotes: String? = null
)

data class UserDocument(
    val id: String,
    val userId: String,
    val documentType: String,
    val fileUrl: String
)

data class ReferralRecord(
    val id: String,
    val referrerId: String,
    val referredUserId: String,
    val status: String,
    val rewardGranted: Boolean = false,
    val referredName: String? = null,
    val createdAt: String? = null
)

data class CompanyAd(
    val id: String,
    val companyName: String,
    val headline: String,
    val description: String,
    val photoUrl: String = "",
    val websiteUrl: String = "",
    val contactPhone: String = "",
    val category: String = "",
    val isActive: Boolean = true
)

data class EducationItem(
    val degree: String = "",
    val school: String = "",
    val startYear: String = "",
    val endYear: String = ""
)

data class ExperienceItem(
    val role: String = "",
    val company: String = "",
    val startYear: String = "",
    val endYear: String = "",
    val achievements: String = ""
)

data class ReportedJobItem(
    val id: String,
    val jobId: String,
    val jobTitle: String,
    val organization: String,
    val reporterEmail: String,
    val reason: String,
    val reportedAt: String
)
