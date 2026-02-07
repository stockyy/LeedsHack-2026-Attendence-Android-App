package com.university.app.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable

// The data we send to the server
@Serializable
data class CheckInRequest(
    val nfcCode: String,
    val moodScore: Int,
    val studentId: Int // Hardcode this to "1" for the hackathon demo if Auth is too hard
)

object ApiClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    // Ben's IP when connected to my hotspot
    private const val BASE_URL = "http://194.80.232.20" 

    suspend fun performCheckIn(nfcText: String, mood: Int): Boolean {
        return try {
            val response = client.post("$BASE_URL/api/attendance/checkin") {
                contentType(ContentType.Application.Json)
                setBody(CheckInRequest(nfcText, mood, 1))
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}