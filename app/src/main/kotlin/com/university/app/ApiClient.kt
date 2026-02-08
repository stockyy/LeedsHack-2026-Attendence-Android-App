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
    val userID: Int
)

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
    suspend fun performCheckIn(nfcText: String, mood: Int, userID: Int): Boolean {
        return try {
            val response = client.post("$BASE_URL/api/attendance/checkin") {
                contentType(ContentType.Application.Json)
                setBody(CheckInRequest(nfcText, mood, userID))
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
