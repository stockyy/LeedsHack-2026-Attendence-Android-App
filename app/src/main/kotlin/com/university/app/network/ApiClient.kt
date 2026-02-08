package com.university.app.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// --- DATA MODELS ---

@Serializable
data class SignInRequest(val email: String, val password: String)

@Serializable
data class AuthResponse(
    val userId: Int,
    val userName: String,
    val role: String,
    val totalPoints: Int = 0, // Ensuring this is here for live stats
    val message: String? = null
)

@Serializable
data class CheckInRequest(
    val nfcCode: String,
    val moodScore: Int,
    val studentId: Int
)

@Serializable
data class RewardTier(
    val tier: Int,
    // val points: Int, // Commented out: Server isn't sending this right now, keeping it simple!
    val reward: String,
    val unlocked: Boolean
)

@Serializable
data class LeaderboardItem(
    val rank: Int,
    val name: String,
    val score: String
)

enum class CheckInResult {
    SUCCESS, ALREADY_CHECKED_IN, NETWORK_ERROR, INVALID_TAG
}

// --- API CLIENT ---

object ApiClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true // CRITICAL: Prevents app crash if Server adds new fields
            })
        }
    }

    // KEEP YOUR NGROK URL
    private const val BASE_URL = "https://zayden-unrecompensed-annie.ngrok-free.dev"

    /**
     * 1. LOGIN
     * Changed URL to /signin to match Routing.kt
     */
    suspend fun signIn(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = client.post("$BASE_URL/api/auth/signin") { // Changed from /api/auth/signin
                contentType(ContentType.Application.Json)
                setBody(SignInRequest(email, password))
            }
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Login failed: ${response.status}"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * 2. GET USER (LIVE REFRESH)
     * NEW: Call this in onResume() to update points without logging in again
     */
    suspend fun getUser(userId: Int): AuthResponse? {
        return try {
            client.get("$BASE_URL/api/user/$userId").body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 3. CHECK-IN (NFC)
     * Kept your logic, but ensure Routing.kt has post("/api/attendance/checkin")
     */
    suspend fun performCheckIn(nfcId: String, mood: Int, studentId: Int): CheckInResult {
        return try {
            val response = client.post("$BASE_URL/api/attendance/checkin") {
                contentType(ContentType.Application.Json)
                setBody(CheckInRequest(nfcId, mood, studentId))
            }
            when (response.status) {
                HttpStatusCode.OK, HttpStatusCode.Created -> CheckInResult.SUCCESS
                HttpStatusCode.Conflict -> CheckInResult.ALREADY_CHECKED_IN
                HttpStatusCode.BadRequest, HttpStatusCode.NotFound -> CheckInResult.INVALID_TAG
                else -> CheckInResult.NETWORK_ERROR
            }
        } catch (e: Exception) {
            e.printStackTrace()
            CheckInResult.NETWORK_ERROR
        }
    }

    /**
     * 4. LEADERBOARD
     * Matches Routing.kt get("/api/leaderboard")
     */
    suspend fun getLeaderboard(): List<LeaderboardItem> {
        return try {
            client.get("$BASE_URL/api/leaderboard").body()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 5. REWARDS
     * Updated: Sends studentId to calculate unlocked status
     */
    suspend fun getRewardTiers(studentId: Int): List<RewardTier> {
        return try {
            client.get("$BASE_URL/api/rewards") {
                parameter("studentId", studentId)
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // --- QUIZ FUNCTIONS (Preserved) ---

    suspend fun getAvailableQuiz(studentId: Int): Quiz? {
        return try {
            val response = client.get("$BASE_URL/api/quiz/available") {
                parameter("studentId", studentId)
            }
            if (response.status == HttpStatusCode.OK) response.body() else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun submitQuiz(submission: QuizSubmission): QuizResponse? {
        return try {
            val response = client.post("$BASE_URL/api/quiz/submit") {
                contentType(ContentType.Application.Json)
                setBody(submission)
            }
            if (response.status == HttpStatusCode.OK) response.body() else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
