package com.university.app.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable

// --- Data Models matching the Backend ---

@Serializable
data class SignInRequest(val email: String, val password: String)

@Serializable
data class AuthResponse(
    val userId: Int,
    val userName: String,
    val role: String,
    val message: String? = null
)

@Serializable
data class CheckInRequest(
    val nfcCode: String,
    val moodScore: Int,
    val studentId: Int
)

enum class CheckInResult {
    SUCCESS,
    ALREADY_CHECKED_IN,
    NETWORK_ERROR,
    INVALID_TAG
}

object ApiClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    // REPLACE THIS WITH YOUR LAPTOP'S ACTUAL IP ADDRESS
    private const val BASE_URL = "https://zayden-unrecompensed-annie.ngrok-free.dev"

    /**
     * Attempts to sign in with email and password.
     * Returns the AuthResponse if successful, or null if failed.
     */
    suspend fun signIn(email: String, password: String): AuthResponse? {
        return try {
            val response = client.post("$BASE_URL/api/auth/signin") {
                contentType(ContentType.Application.Json)
                setBody(SignInRequest(email, password))
            }
            if (response.status == HttpStatusCode.OK) {
                // Return the parsed body
                response.body<AuthResponse>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Sends the NFC scan data to the backend.
     */
    suspend fun performCheckIn(nfcId: String, mood: Int, studentId: Int): CheckInResult {
        return try {
            println("ApiClient: Sending Check-in for student $studentId with tag $nfcId")
            val response = client.post("$BASE_URL/api/attendance/checkin") {
                contentType(ContentType.Application.Json)
                setBody(CheckInRequest(nfcId, mood, studentId))
            }
            println("ApiClient: Response Status: ${response.status}")
            
            when (response.status) {
                HttpStatusCode.OK, HttpStatusCode.Created -> CheckInResult.SUCCESS
                HttpStatusCode.Conflict -> CheckInResult.ALREADY_CHECKED_IN
                HttpStatusCode.BadRequest, HttpStatusCode.NotFound -> CheckInResult.INVALID_TAG
                else -> CheckInResult.NETWORK_ERROR
            }
        } catch (e: Exception) {
            println("ApiClient: Error during check-in: ${e.message}")
            e.printStackTrace()
            CheckInResult.NETWORK_ERROR
        }
    }

    /**
     * Fetches the available quiz for a student.
     */
    suspend fun getAvailableQuiz(studentId: Int): Quiz? {
        return try {
            val response = client.get("$BASE_URL/api/quiz/available") {
                parameter("studentId", studentId)
            }
            if (response.status == HttpStatusCode.OK) {
                response.body<Quiz>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Submits quiz answers to the backend.
     */
    suspend fun submitQuiz(submission: QuizSubmission): QuizResponse? {
        return try {
            val response = client.post("$BASE_URL/api/quiz/submit") {
                contentType(ContentType.Application.Json)
                setBody(submission)
            }
            if (response.status == HttpStatusCode.OK) {
                response.body<QuizResponse>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Fetches student statistics.
     */
    suspend fun getStudentStats(studentId: Int): StudentStats? {
        return try {
            val response = client.get("$BASE_URL/api/stats/student") {
                parameter("studentId", studentId)
            }
            if (response.status == HttpStatusCode.OK) {
                response.body<StudentStats>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
