package com.university.app.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.* // Import for bodyAsText if needed
import io.ktor.client.call.* // Import for body()
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable

// --- DATA CLASSES ---

@Serializable
data class CheckInRequest(
    val nfcCode: String,
    val moodScore: Int,
    val studentId: Int
)

@Serializable
data class SignInRequest(val email: String, val password: String)

@Serializable
data class AuthResponse(
    val userId: Int,
    val userName: String,
    val role: String,
    val message: String? = null
)

// --- CLIENT OBJECT ---

object ApiClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    // ⚠️ IMPORTANT: Ensure this is your computer's IP or Ngrok URL
    private const val BASE_URL = "https://zayden-unrecompensed-annie.ngrok-free.dev"

    // 1. LOGIN FUNCTION
    suspend fun login(email: String, pass: String): AuthResponse? {
        return try {
            val response = client.post("$BASE_URL/api/auth/signin") {
                contentType(ContentType.Application.Json)
                setBody(SignInRequest(email, pass))
            }
            if (response.status == HttpStatusCode.OK) {
                // Return the user data (ID, Name, Role)
                response.body<AuthResponse>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 2. CHECK-IN FUNCTION
    suspend fun performCheckIn(nfcText: String, mood: Int, userId: Int): Boolean {
        return try {
            val response = client.post("$BASE_URL/api/attendance/checkin") {
                contentType(ContentType.Application.Json)
                // Pass the REAL userId here!
                setBody(CheckInRequest(nfcText, mood, userId))
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}